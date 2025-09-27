package org.animefoda.user.entities.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.animefoda.user.exception.BadCredentialsException;
import org.animefoda.user.exception.BadRequestException;
import org.animefoda.user.exception.ReCaptchaException;
import org.animefoda.user.request.login.LoginEncrypted;
import org.animefoda.user.request.login.LoginRequest;
import org.animefoda.user.response.ApiResponse;
import org.animefoda.user.response.GoogleResponse;
import org.animefoda.user.response.TokenResponse;
import org.animefoda.user.services.KeysService;
import org.animefoda.user.services.ReCaptchaService;
import org.animefoda.user.services.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
public class UserController {

    private final UserService userService;
    private final KeysService keysService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ReCaptchaService reCaptchaService;
    private final ValidationService validationService;

    public UserController(
            UserService userService,
            KeysService keysService,
            ReCaptchaService reCaptchaService,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            ValidationService validationService
    ) {
        this.userService = userService;
        this.keysService = keysService;
        this.reCaptchaService = reCaptchaService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.validationService = validationService;
    }

    @PostMapping("login")
    public ApiResponse<TokenResponse> login(
        @RequestBody LoginEncrypted body,
        @RequestHeader("User-Agent") String userAgent
    ) throws Exception{
        if(body.encryptedInfo() == null) throw new BadRequestException("Request Error","Encrypted info is null");
        if(body.recaptchaToken() == null) throw new BadRequestException("Request Error","Recaptcha Token is null");

        LoginRequest request = keysService.decryptAndDeserialize(body.encryptedInfo(), LoginRequest.class);
        GoogleResponse googleResponse = reCaptchaService.processResponse(body.recaptchaToken());
        if(!googleResponse.success()) throw new ReCaptchaException();

        User user;

        if(this.validationService.validateEmail(request.loginValue())){
            user = userService.findByEmail(request.loginValue()).orElseThrow(BadCredentialsException::new);
        }else if(this.validationService.validateUsername(request.loginValue())){
            user = userService.findByUsername(request.loginValue()).orElseThrow(BadCredentialsException::new);
        }else{
            throw new BadCredentialsException();
        }

        if (!user.isLoginCorrect(request.password(), bCryptPasswordEncoder)) throw new BadCredentialsException();

        UserSession session = userSessionService.createSession(user);
        session.setUserAgent(userAgent);
        session.setFingerprint(request.fingerprint());

        userSessionService.save(session);

        return ApiResponse.setSuccess(null);
    }
}
