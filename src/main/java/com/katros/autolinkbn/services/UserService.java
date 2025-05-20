package com.katros.autolinkbn.services;

import com.katros.autolinkbn.dtos.*;
import com.katros.autolinkbn.entities.EmailOTP;
import com.katros.autolinkbn.entities.RoleChangeRequest;
import com.katros.autolinkbn.entities.User;
import com.katros.autolinkbn.enums.Gender;
import com.katros.autolinkbn.enums.RequestStatus;
import com.katros.autolinkbn.enums.Role;
import com.katros.autolinkbn.enums.Status;
import com.katros.autolinkbn.exceptions.BadRequestException;
import com.katros.autolinkbn.exceptions.ConflictException;
import com.katros.autolinkbn.exceptions.NotFoundException;
import com.katros.autolinkbn.repositories.EmailOTPRepository;
import com.katros.autolinkbn.repositories.RoleChangeRequestRepository;
import com.katros.autolinkbn.repositories.UserRepository;
import com.katros.autolinkbn.services.customvalidations.FileValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private static final String DEFAULT_MALE_PLACEHOLDER_IMAGE_URL = "https://res.cloudinary.com/dboqnapgi/image/upload/v1738663210/profile_pics/default-male-avatar-profile_nxtywh.jpg";
    private static final String DEFAULT_FEMALE_PLACEHOLDER_IMAGE_URL = "https://res.cloudinary.com/dboqnapgi/image/upload/v1738663210/profile_pics/default-female-avatar-profile_dvmnyx.jpg";

    private final UserRepository userRepository;
    private final EmailValidationService emailValidationService;
    private final EmailService emailService;
    private final PasswordService passwordService;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final FileValidationService fileValidationService;
    private final RoleChangeRequestRepository roleChangeRequestRepository;
    private final NotificationService notificationService;
    private final EmailOTPRepository emailOTPRepository;

    public User registerUser(UserDTO userRegistrationDTO) {

        emailValidationService.validateEmailFormat(userRegistrationDTO.getEmail());
        emailValidationService.checkEmailAlreadyExists(userRegistrationDTO.getEmail(), userRepository);

        User user = new User();
        user.setFirstName(userRegistrationDTO.getFirstName());
        user.setLastName(userRegistrationDTO.getLastName());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPhoneNumber(userRegistrationDTO.getPhoneNumber());
        user.setDob(userRegistrationDTO.getDob());
        user.setGender(userRegistrationDTO.getGender());
        user.setCountry(userRegistrationDTO.getCountry());
        user.setVerified(false);
        user.setAccountStatus(Status.ACTIVE);

        user.setRoles(new HashSet<>(Collections.singleton(Role.CLIENT)));

        user.setPassword(passwordService.hashPassword(userRegistrationDTO.getPassword()));

        user.setProfilePicUrl(userRegistrationDTO.getGender().equals(Gender.MALE)
                ? DEFAULT_MALE_PLACEHOLDER_IMAGE_URL
                : DEFAULT_FEMALE_PLACEHOLDER_IMAGE_URL);

        User savedUser = userRepository.save(user);

        sendVerificationOTP(savedUser.getEmail());

        return savedUser;
    }

    public void sendVerificationOTP(String email) {
        String otp = String.format("%05d", new Random().nextInt(100000));

        emailOTPRepository.deleteByEmail(email); // Remove any existing OTP
        EmailOTP emailOTP = new EmailOTP();
        emailOTP.setEmail(email);
        emailOTP.setOtp(otp);
        emailOTPRepository.save(emailOTP);

        // Prepare email
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getFirstName());
        variables.put("otp", otp);

        emailService.sendWelcomingEmail(email, otp);
    }


    public UserProfileDTO getUserProfile(String userId) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    UserProfileDTO userProfile = new UserProfileDTO();

                    BeanUtils.copyProperties(existingUser, userProfile);
                    return userProfile;

                })
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    public String verifyEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new NotFoundException("User not found with email: " + email);
        }

        User user = userOptional.get();

        if (user.isVerified()) {
            throw new ConflictException("User already verified");
        }

        user.setVerified(true);
        userRepository.save(user);

        return "Email verified successfully!";
    }

    public String verifyOtp(String email, String otp) {
        EmailOTP emailOtp = emailOTPRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("OTP not found for email"));

        if (emailOtp.isExpired()) {
            emailOTPRepository.deleteByEmail(email);
            throw new BadRequestException("OTP has expired");
        }

        if (!emailOtp.getOtp().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setVerified(true);
        userRepository.save(user);

        emailOTPRepository.deleteByEmail(email);
        return "Email verified successfully!";
    }

    public Page<User> getAllUsers(Optional<Role> role, Pageable pageable) {
        if (role.isPresent()) {
            return userRepository.findByRolesContaining(role.get(), pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    public Optional<User> getUserById(String id) {

        return userRepository.findById(id);
    }

    public User updateUser(String id, User updatedUser) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    updateNonNullFields(existingUser, updatedUser);
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private void updateNonNullFields(User existingUser, User updatedUser) {
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(User.class)) {
            try {
                Method getter = propertyDescriptor.getReadMethod();
                Method setter = propertyDescriptor.getWriteMethod();

                if (getter != null && setter != null) {
                    Object newValue = getter.invoke(updatedUser);
                    if (newValue != null) {
                        setter.invoke(existingUser, newValue);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to update field: " + propertyDescriptor.getName(), e);
            }
        }
    }

    public User updateUser(String userId, UpdateUserDTO updatedUser) {
        return userRepository.findById(userId)
                .map(existingUser -> {
                    updateNonNullUserFields(existingUser, updatedUser);
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    public void updatePassword(String userId, UpdatePasswordDTO updatePasswordDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    public User updateProfilePicture(String userId, MultipartFile profilePicture) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        fileValidationService.validateImageFile(profilePicture);
        String uploadedImageFile = cloudinaryService.uploadImageFile(profilePicture, user.getLastName());
        user.setProfilePicUrl(uploadedImageFile);
        return userRepository.save(user);
    }

    private void updateNonNullUserFields(User existingUser, UpdateUserDTO updatedUser) {
        for (PropertyDescriptor propertyDescriptor : BeanUtils.getPropertyDescriptors(User.class)) {
            try {
                if (BeanUtils.getPropertyDescriptor(UpdateUserDTO.class, propertyDescriptor.getName()) == null) {
                    continue; // Skip fields that are not in the DTO
                }

                Method getter = UpdateUserDTO.class.getMethod(propertyDescriptor.getReadMethod().getName());
                Method setter = propertyDescriptor.getWriteMethod();

                if (getter != null && setter != null) {
                    Object newValue = getter.invoke(updatedUser);
                    if (newValue != null) {
                        // Handle Enum conversion
                        if (propertyDescriptor.getPropertyType().isEnum() && newValue instanceof String) {
                            newValue = Enum.valueOf((Class<Enum>) propertyDescriptor.getPropertyType(), (String) newValue);
                        }
                        setter.invoke(existingUser, newValue);
                    }
                }
            } catch (Exception e) {
                log.error("Failed to update field: {}", propertyDescriptor.getName(), e);
                throw new RuntimeException("Failed to update field: " + propertyDescriptor.getName(), e);
            }
        }
    }

    public void makeRoleChangeRequest(String userId, Role requestedRole) {

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRoles().contains(requestedRole)) {
            throw new BadRequestException("You already have this role.");
        }

        RoleChangeRequest request = new RoleChangeRequest();
        request.setUser(user);
        request.setRequestedRole(requestedRole);

        roleChangeRequestRepository.save(request);
    }

    public void approveRoleChangeRequest(String requestId, boolean approve) {

        RoleChangeRequest request = roleChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Role request not found"));

        if (approve) {
            User user = request.getUser();
            user.getRoles().add(request.getRequestedRole()); // Add the new role
            userRepository.save(user);
            request.setStatus(RequestStatus.APPROVED);
        } else {
            request.setStatus(RequestStatus.REJECTED);
        }

        roleChangeRequestRepository.save(request);
    }

    public List<RoleChangeRequestDTO> getRoleChangeRequest() {
        List<RoleChangeRequest> pendingRequests = roleChangeRequestRepository.findByStatus(RequestStatus.PENDING);
        return pendingRequests.stream()
                .map(request -> new RoleChangeRequestDTO(
                        request.getId(),
                        request.getUser().getLastName() +
                                request.getUser().getFirstName(),
                        request.getUser().getEmail(),
                        request.getUser().getProfilePicUrl(),
                        request.getRequestedRole(),
                        request.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public User deactivateUser(String userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setAccountStatus(Status.INACTIVE);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    public User activateUser(String userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setAccountStatus(Status.ACTIVE);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }
}

