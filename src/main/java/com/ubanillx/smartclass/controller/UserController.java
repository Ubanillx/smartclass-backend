package com.ubanillx.smartclass.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ubanillx.smartclass.annotation.AuthCheck;
import com.ubanillx.smartclass.common.BaseResponse;
import com.ubanillx.smartclass.common.DeleteRequest;
import com.ubanillx.smartclass.common.ErrorCode;
import com.ubanillx.smartclass.common.ResultUtils;
import com.ubanillx.smartclass.config.WxOpenConfig;
import com.ubanillx.smartclass.constant.UserConstant;
import com.ubanillx.smartclass.exception.BusinessException;
import com.ubanillx.smartclass.exception.ThrowUtils;
import com.ubanillx.smartclass.model.dto.user.*;
import com.ubanillx.smartclass.model.entity.User;
import com.ubanillx.smartclass.model.vo.LoginUserVO;
import com.ubanillx.smartclass.model.vo.UserVO;
import com.ubanillx.smartclass.service.UserService;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ubanillx.smartclass.constant.UserConstant.SALT;


/**
 * 用户接口
*/
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    // region 登录相关

/**
 * 用户注册接口
 *
 * @param userRegisterRequest 用户注册请求体，包含用户账户、密码和确认密码等信息
 * @return BaseResponse<Long> 注册成功后返回用户的唯一标识 ID
 * @throws BusinessException 如果请求参数为空或无效，抛出业务异常
 */
@PostMapping("/register")
public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
    if (userRegisterRequest == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    String userAccount = userRegisterRequest.getUserAccount();
    String userPassword = userRegisterRequest.getUserPassword();
    String checkPassword = userRegisterRequest.getCheckPassword();
    if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
        return null;
    }
    long result = userService.userRegister(userAccount, userPassword, checkPassword);
    return ResultUtils.success(result);
}

   /**
    * 用户手机注册
    *
    * @param userRegisterByPhoneRequest 包含用户手机号、密码和确认密码的请求对象
    * @return 注册结果
    */
   @PostMapping("/register/phone")
   public BaseResponse<Long> userRegisterByPhone(@RequestBody UserRegisterByPhoneRequest userRegisterByPhoneRequest) {
       if (userRegisterByPhoneRequest == null) {
           throw new BusinessException(ErrorCode.PARAMS_ERROR);
       }
       String userPhone = userRegisterByPhoneRequest.getUserPhone();
       String userPassword = userRegisterByPhoneRequest.getUserPassword();
       String checkPassword = userRegisterByPhoneRequest.getCheckPassword();
       if (StringUtils.isAnyBlank(userPhone, userPassword, checkPassword)) {
           return null;
       }
       long result = userService.userRegisterByPhone(userPhone, userPassword, checkPassword);
       return ResultUtils.success(result);
   }



/**
 * 用户登录接口
 *
 * <p>该接口用于处理用户的登录请求，用户需要提供有效的账户名和密码。</p>
 *
 * @param userLoginRequest 包含用户账户和密码的请求体
 * @param request          当前 HTTP 请求对象，用于传递会话信息
 * @return BaseResponse<LoginUserVO> 登录成功后返回封装的用户登录信息(LoginUserVO)
 * @throws BusinessException 如果请求参数为空或无效，抛出业务异常
 */
@PostMapping("/login")
public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
    if (userLoginRequest == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    String userAccount = userLoginRequest.getUserAccount();
    String userPassword = userLoginRequest.getUserPassword();
    if (StringUtils.isAnyBlank(userAccount, userPassword)) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
    return ResultUtils.success(loginUserVO);
}


/**
 * 用户通过手机号登录接口
 *
 * <p>该接口用于处理用户使用手机号和密码进行登录的请求。</p>
 *
 * @param userLoginByPhoneRequest 包含用户手机号和密码的请求体
 * @param request                 当前 HTTP 请求对象，用于传递会话信息
 * @return BaseResponse<LoginUserVO> 登录成功后返回封装的用户登录信息 (LoginUserVO)
 * @throws BusinessException 如果请求参数为空或无效，抛出业务异常
 */
@PostMapping("/login/phone")
public BaseResponse<LoginUserVO> userLoginByPhone(@RequestBody UserLoginByPhoneRequest userLoginByPhoneRequest, HttpServletRequest request) {
    if (userLoginByPhoneRequest == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    String userPhone = userLoginByPhoneRequest.getUserPhone();
    String userPassword = userLoginByPhoneRequest.getUserPassword();
    if (StringUtils.isAnyBlank(userPhone, userPassword)) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    LoginUserVO loginUserVO = userService.userLoginByPhone(userPhone, userPassword, request);
    return ResultUtils.success(loginUserVO);
}


/**
 * 用户登录（微信开放平台）
 *
 * @param request HTTP 请求对象
 * @param code 微信授权登录的临时凭证码
 * @return 登录用户的信息
 */
@GetMapping("/login/wx_open")
public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request,
        @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ResultUtils.success(userService.userLoginByMpOpen(userInfo, request));
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

/**
 * 用户注销接口
 *
 * <p>该接口用于处理用户的注销请求，清除当前用户的登录状态。</p>
 *
 * @param request 当前 HTTP 请求对象，用于获取会话信息
 * @return BaseResponse<Boolean> 注销成功后返回 true，表示用户已成功退出系统
 * @throws BusinessException 如果请求为空或无效，抛出参数错误异常
 */
@PostMapping("/logout")
public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
    if (request == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    boolean result = userService.userLogout(request);
    return ResultUtils.success(result);
}


/**
 * 获取当前登录用户信息接口
 *
 * <p>该接口用于获取当前已登录用户的详细信息，返回封装后的用户视图对象（LoginUserVO）。</p>
 *
 * @param request 当前 HTTP 请求对象，用于获取会话中的用户信息
 * @return BaseResponse<LoginUserVO> 返回封装后的当前登录用户信息
 * @throws BusinessException 如果请求为空或无法获取当前用户，抛出业务异常
 */
@GetMapping("/get/login")
public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
    User user = userService.getLoginUser(request);
    return ResultUtils.success(userService.getLoginUserVO(user));
}



/**
 * 创建用户接口（仅管理员可用）
 *
 * <p>该接口用于创建新用户，只有管理员角色有权限调用此接口。</p>
 *
 * @param userAddRequest 用户创建请求对象，包含用户的属性信息
 * @return BaseResponse<Long> 创建成功后返回新用户的唯一标识 ID
 * @throws BusinessException 如果请求参数为空、保存失败或权限不足，抛出相应的业务异常
 */
@PostMapping("/add")
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
    if (userAddRequest == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    User user = new User();
    BeanUtils.copyProperties(userAddRequest, user);

    // 设置默认密码并加密存储
    String defaultPassword = "12345678";
    String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
    user.setUserPassword(encryptPassword);

    boolean result = userService.save(user);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(user.getId());
}


/**
 * 删除用户接口（仅管理员可用）
 *
 * <p>该接口用于根据用户 ID 删除指定用户，只有管理员角色有权限调用此接口。</p>
 *
 * @param deleteRequest 包含待删除用户 ID 的请求对象
 * @return BaseResponse<Boolean> 删除成功返回 true，表示用户已被成功删除
 * @throws BusinessException 如果请求参数为空、ID 无效或删除失败，抛出相应的业务异常
 */
@PostMapping("/delete")
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
    if (deleteRequest == null || deleteRequest.getId() <= 0) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    boolean b = userService.removeById(deleteRequest.getId());
    return ResultUtils.success(b);
}


/**
 * 更新用户信息接口（仅管理员可用）
 *
 * <p>该接口用于根据用户 ID 更新指定用户的详细信息，只有管理员角色有权限调用此接口。</p>
 *
 * @param userUpdateRequest 包含用户更新信息的请求对象，必须包含有效的用户 ID 和更新字段
 * @return BaseResponse<Boolean> 更新成功返回 true，表示用户信息已成功更新
 * @throws BusinessException 如果请求参数为空、ID 无效或更新失败，抛出相应的业务异常
 */
@PostMapping("/update")
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
    if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    User user = new User();
    BeanUtils.copyProperties(userUpdateRequest, user);
    boolean result = userService.updateById(user);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(true);
}


/**
 * 根据用户 ID 获取用户信息（管理员可查看任意用户，普通用户仅能查看自己）
 *
 * <p>该接口用于根据用户 ID 查询对应的用户信息。</p>
 * <ul>
 *   <li>管理员角色：可以查看系统中任意用户的信息</li>
 *   <li>普通用户：只能查看自己的信息，若尝试访问其他用户将返回无权限错误</li>
 * </ul>
 *
 * @param id      要查询的用户 ID，必须大于 0
 * @param request 当前 HTTP 请求对象，用于获取当前登录用户及鉴权
 * @return BaseResponse<User> 返回查询到的用户信息
 * @throws BusinessException 如果参数无效、无权限或用户不存在，抛出相应的业务异常
 */
@GetMapping("/get")
public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
    if (id <= 0) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }

    // 获取当前登录用户
    User loginUser = userService.getLoginUser(request);

    // 如果不是管理员，则只能查看自己的信息
    if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(id)) {
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看其他用户信息");
    }

    User user = userService.getById(id);
    ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
    return ResultUtils.success(user);
}


/**
 * 根据用户 ID 获取封装视图对象（UserVO）
 *
 * <p>该接口用于根据用户 ID 查询用户信息，并返回封装后的用户视图对象 UserVO。</p>
 * <ul>
 *   <li>管理员角色：可以查看任意用户的详细信息</li>
 *   <li>普通用户：只能查看自己的信息，若访问其他用户将抛出无权限异常</li>
 * </ul>
 *
 * @param id      用户 ID，必须大于 0
 * @param request 当前 HTTP 请求对象，用于鉴权和获取当前登录用户
 * @return BaseResponse<UserVO> 返回封装后的用户视图对象
 * @throws BusinessException 如果参数无效、无权限或用户不存在，抛出相应的业务异常
 */
@GetMapping("/get/vo")
public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
    BaseResponse<User> response = getUserById(id, request);
    User user = response.getData();
    return ResultUtils.success(userService.getUserVO(user));
}


/**
 * 分页获取用户列表接口（仅管理员可用）
 *
 * <p>该接口用于分页查询系统中的用户列表，只有管理员角色有权限调用此接口。</p>
 *
 * @param userQueryRequest 分页查询请求对象，包含当前页码和每页大小等信息
 * @return BaseResponse<Page<User>> 返回分页封装的用户列表数据
 * @throws BusinessException 如果请求参数为空或无效，抛出业务异常
 */
@PostMapping("/list/page")
@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest) {
    long current = userQueryRequest.getCurrent();
    long size = userQueryRequest.getPageSize();
    Page<User> userPage = userService.page(new Page<>(current, size),
            userService.getQueryWrapper(userQueryRequest));
    return ResultUtils.success(userPage);
}


/**
 * 分页获取用户封装视图列表（仅管理员可用）
 *
 * <p>该接口用于分页查询用户信息，并返回经过封装处理的用户视图对象（UserVO）。</p>
 * <p>默认对每页数据量进行限制（最大为 20），防止恶意爬虫或大量数据请求。</p>
 *
 * @param userQueryRequest 分页查询请求体，包含当前页码、每页大小等查询参数
 * @return BaseResponse<Page<UserVO>> 返回封装后的分页用户视图对象列表
 * @throws BusinessException 如果请求参数为空、页大小超过限制或权限不足，抛出相应的业务异常
 */
@PostMapping("/list/page/vo")
public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
    if (userQueryRequest == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    long current = userQueryRequest.getCurrent();
    long size = userQueryRequest.getPageSize();

    // 防止数据泄露或爬虫攻击，限制单页最大记录数
    ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

    Page<User> userPage = userService.page(new Page<>(current, size),
            userService.getQueryWrapper(userQueryRequest));

    Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
    List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
    userVOPage.setRecords(userVO);

    return ResultUtils.success(userVOPage);
}



/**
 * 更新当前登录用户个人信息接口
 *
 * <p>该接口用于更新当前登录用户的个人资料信息（如昵称、头像、邮箱等），不包括密码。</p>
 * <p>普通用户只能更新自己的信息，管理员用户也无法通过此接口修改其他用户的信息。</p>
 *
 * @param userUpdateMyRequest 用户个人信息更新请求对象，包含需要更新的字段数据
 * @param request             当前 HTTP 请求对象，用于获取当前登录用户的身份信息
 * @return BaseResponse<Boolean> 更新成功返回 true，表示用户信息已成功修改
 * @throws BusinessException 如果请求参数为空、更新失败或当前用户未登录，抛出相应的业务异常
 */
@PostMapping("/update/my")
public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
        HttpServletRequest request) {
    if (userUpdateMyRequest == null) {
        throw new BusinessException(ErrorCode.PARAMS_ERROR);
    }
    User loginUser = userService.getLoginUser(request);
    User user = new User();
    BeanUtils.copyProperties(userUpdateMyRequest, user);
    user.setId(loginUser.getId());
    boolean result = userService.updateById(user);
    ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    return ResultUtils.success(true);
}

}
