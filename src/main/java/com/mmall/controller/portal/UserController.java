package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by guolin
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    // iUserService与service层的@Service("iUserService")名字对应
    @Autowired
    private IUserService iUserService;

    /***
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody // 自动通过spring mvc 插件，将返回值序列化成json
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){
        // service-->mybatis-->dao
        ServerResponse<User> response = iUserService.login(username, password);

        if(response.isSuccess()){
//            session.setAttribute(Const.CURRENT_USER, response.getData());

            CookieUtil.writeLoginToken(httpServletResponse,session.getId());

            // 把Token和用户信息的字符串存入redis
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 登出
     * @param httpServletRequest
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody // 自动通过spring mvc 插件，将返回值序列化成json
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse) {
//        session.removeAttribute(Const.CURRENT_USER);

        // 拿到Token
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // 删除Cookie
        CookieUtil.delLoginToken(httpServletRequest,httpServletResponse);

        // 删除redis中的Token
        RedisShardedPoolUtil.del(loginToken);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 实时校验email和用户名是否存在，给前台一个实时的反馈
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取已登录用户的信息
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest httpServletRequest) {
//        User user = (User) session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        // 用户已登录
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }

        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }

    /**
     * 忘记密码
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        // 返回找回密码的问题
        return iUserService.selectQuestion(username);
    }

    /**
     * 忘记密码时，验证问题找回密码问题答案是否正确
     * @param username
     * @param question
     * @param answer
     * @return 如果正确，则会返回token
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码时，重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态下，重置密码
     * @param httpServletRequest
     * @param passwordOld
     * @param passwordNew
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest, String passwordOld, String passwordNew) {
        // 从session中获取用户
//        User user = (User) session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    /**
     * 更新用户个人信息
     * @param httpServletRequest
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpServletRequest httpServletRequest, User user) {
//        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);

        if(currentUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }

        // user只能改email，问题，答案；没有userId和username，所以要将当前登录的id和name放进去
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());

        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
//            session.setAttribute(Const.CURRENT_USER, response.getData());

            // 把Token和用户信息的字符串存入redis
            RedisShardedPoolUtil.setEx(loginToken, JsonUtil.obj2String(response.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    /**
     * 获取用户详情信息
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest httpServletRequest){
//        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);

        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
