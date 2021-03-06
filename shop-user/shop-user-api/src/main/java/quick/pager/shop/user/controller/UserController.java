package quick.pager.shop.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quick.pager.shop.constants.Constants;
import quick.pager.shop.constants.RedisKeys;
import quick.pager.shop.constants.ResponseStatus;
import quick.pager.shop.response.Response;
import quick.pager.shop.service.RedisService;
import quick.pager.shop.user.param.UserLoginParam;
import quick.pager.shop.user.service.UserService;

/**
 * 用户管理<br />
 * 登陆 注册 退出 用户信息 忘记密码
 *
 * @author siguiyang
 */
@RestController
@RequestMapping(Constants.Module.USER)
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;


    /**
     * 登陆
     */
    @PostMapping("/login")
    public Response login(@RequestBody UserLoginParam param) {
        String key = RedisKeys.UserKeys.SHOP_LOGIN + param.getPhone();

        if (null != redisService.get(key)) {
            return new Response<>(ResponseStatus.Code.FAIL_CODE, ResponseStatus.REPEAT_SUBMIT);
        }

        // 密码为空 则使用短信验证码登陆
        if (StringUtils.isEmpty(param.getPassword())) {
            log.info("密码为空，使用短信验证码登陆 phone = {}", param.getPhone());
            // 验证短信验证码
            String smsKey = RedisKeys.UserKeys.SHOP_LOGIN_SMS + param.getPhone();

            String smsCode = redisService.get(smsKey);

            // 短信验证码过期
            if (StringUtils.isEmpty(smsCode)) {
                return new Response<>(ResponseStatus.Code.FAIL_CODE, ResponseStatus.SMS_CODE_EXPIRE);
            }

            // 短信验证码是否匹配
            if (!smsCode.equalsIgnoreCase(param.getVerifyCode())) {
                return new Response<>(ResponseStatus.Code.FAIL_CODE, ResponseStatus.SMS_CODE_ERROR);
            }
        }
        return userService.login(param.getPhone(), param.getPassword());
    }
//
//    /**
//     * 修改用户信息
//     */
//    @PostMapping("/edit")
//    public Response edit(@RequestBody @Valid UserInfoDTO request, BindingResult bindingResult) {
//        BindingResultUtils.getFieldErrorMessage(bindingResult);
//        String key = RedisKeys.UserKeys.SHOP_MODIFY_USER_INFO + request.getUserId();
//
//        if (!StringUtils.isEmpty(redisService.get(key))) {
//            return new Response(ResponseStatus.Code.FAIL_CODE, ResponseStatus.REPEAT_SUBMIT);
//        }
//
//        redisService.set(key, Constants.Common.ONE, 30);
//
//        return userModifyService.doService(request);
//    }
//
//    /**
//     * 注册
//     */
//    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
//    public Response subscribe(@RequestBody @Valid UserSubscribeDTO request, BindingResult bindingResult) {
//        BindingResultUtils.getFieldErrorMessage(bindingResult);
//        String key = RedisKeys.UserKeys.SHOP_REGISTER + request.getPhone();
//
//        Response verifySubscribeResp = verifySubscribe(key, request.getPhone(), request.getVerifyCode());
//
//        if (ResponseStatus.Code.SUCCESS != verifySubscribeResp.getCode()) {
//            return verifySubscribeResp;
//        }
//
//        UserSubscribeDTO dto = new UserSubscribeDTO();
//        dto.setVerifyCode(request.getVerifyCode());
//        dto.setPhone(request.getPhone());
//
//        redisService.set(key, request.getPhone(), 30);
//
//        return userSubscribeService.doService(dto);
//    }
//
//    /**
//     * 注册验证
//     *
//     * @param key        重复注册的redis key
//     * @param phone      手机号码
//     * @param verifyCode 手机短信验证码
//     */
//    private Response verifySubscribe(String key, String phone, String verifyCode) {
//
//        if (null != redisService.get(key)) {
//            return new Response(ResponseStatus.Code.FAIL_CODE, ResponseStatus.REPEAT_SUBMIT);
//
//        }
//
//        if (StringUtils.isEmpty(verifyCode)) {
//
//            return new Response(ResponseStatus.Code.FAIL_CODE, ResponseStatus.SMS_CODE_NOT_EMPTY);
//        }
//
//        // 验证短信验证码
//        String smsKey = RedisKeys.UserKeys.SHOP_REGISTER_SMS + phone;
//
//        String smsCode = redisService.get(smsKey);
//
//        if (StringUtils.isEmpty(smsCode)) {
//            return new Response(ResponseStatus.Code.FAIL_CODE, ResponseStatus.SMS_CODE_EXPIRE);
//
//        }
//
//        if (!smsCode.equalsIgnoreCase(verifyCode)) {
//            return new Response(ResponseStatus.Code.FAIL_CODE, ResponseStatus.SMS_CODE_ERROR);
//        }
//
//        return new Response();
//    }
//
//    /**
//     * 退出
//     */
//    @RequestMapping(value = "/logout/{userId}", method = RequestMethod.POST)
//    public Response logout(@PathVariable("userId") Long userId) {
//        redisService.del(String.valueOf(userId));
//        return new Response();
//    }
//
//    /**
//     * 用户信息
//     */
//    @RequestMapping(value = "/info/{userId}", method = RequestMethod.POST)
//    public Response userInfo(@PathVariable("userId") Long userId) {
//        UserInfoDTO dto = new UserInfoDTO();
//        dto.setId(userId);
//        return userInfoService.doService(dto);
//    }
//
//    /**
//     * 忘记密码
//     */
//    @RequestMapping(value = "/forget/password", method = RequestMethod.POST)
//    public Response forgetPassword(@RequestBody @Valid ForgetPasswordDTO request, BindingResult bindingResult) {
//        BindingResultUtils.getFieldErrorMessage(bindingResult);
//
//        if (StringUtils.isEmpty(request.getPhone())) {
//            return new Response(ResponseStatus.Code.FAIL_CODE, ResponseStatus.PARAMS_EXCEPTION);
//        }
//
//        // 验证短信验证码
//        String smsKey = RedisKeys.UserKeys.SHOP_FORGET_PASSWORD_SMS + request.getPhone();
//
//        String smsCode = redisService.get(smsKey);
//
//        if (StringUtils.isEmpty(smsCode)) {
//            return new Response<>(ResponseStatus.Code.FAIL_CODE, ResponseStatus.SMS_CODE_EXPIRE);
//
//        }
//
//        if (!smsCode.equalsIgnoreCase(request.getVerifyCode())) {
//            return new Response<>(ResponseStatus.Code.FAIL_CODE, ResponseStatus.SMS_CODE_ERROR);
//        }
//
//        ForgetPasswordDTO dto = new ForgetPasswordDTO();
//        dto.setPhone(request.getPhone());
//
//        return userForgetPasswordService.doService(dto);
//    }
//
//    /**
//     * 站内信列表
//     */
//    @RequestMapping(value = "/station/message", method = RequestMethod.POST)
//    public Response message(@RequestBody @Valid AppRequest request, BindingResult bindingResult) {
//        BindingResultUtils.getFieldErrorMessage(bindingResult);
//        StationMessageDTO dto = new StationMessageDTO();
//        dto.setId(request.getUserId());
//        dto.setEvent(request.getEvent());
//        dto.setPageSize(request.getPageSize());
//        dto.setPage(request.getPage());
//        return stationMessageService.doService(dto);
//    }
//
//    /**
//     * 未读站内信个数
//     */
//    @PostMapping("/station/count")
//    public Response messageCount(@PathVariable("userId") Long userId) {
//        StationMessageDTO stationMessageDTO = new StationMessageDTO();
//        stationMessageDTO.setUserId(userId);
//        stationMessageDTO.setEvent("count");
//        return stationMessageService.doService(stationMessageDTO);
//    }
}

