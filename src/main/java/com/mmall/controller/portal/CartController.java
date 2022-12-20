package com.mmall.controller.portal;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by guolin
 */

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    /**
     * 查购物车List列表
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVo> list(HttpServletRequest httpServletRequest){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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


        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    /**
     * 购物车添加商品
     * @param httpServletRequest
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> add(HttpServletRequest httpServletRequest, Integer count, Integer productId){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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


        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }

    /**
     * 更新购物车
     * @param httpServletRequest
     * @param count
     * @param productId
     * @return
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVo> update(HttpServletRequest httpServletRequest, Integer count, Integer productId){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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

        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),productId,count);
    }

    /**
     * 在购物车中删除产品
     * @param httpServletRequest
     * @param productIds 传一个字符串，用 , 分割
     * @return
     */
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse<CartVo> deleteProduct(HttpServletRequest httpServletRequest,String productIds){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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

        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    /**
     * 购物车全选
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> selectAll(HttpServletRequest httpServletRequest){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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

        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    /**
     * 购物车全反选
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelectAll(HttpServletRequest httpServletRequest){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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

        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
    }

    /**
     * 单独选某个商品
     * @param httpServletRequest
     * @param productId
     * @return
     */
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVo> select(HttpServletRequest httpServletRequest,Integer productId){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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

        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    /**
     * 单独反选某个商品
     * @param httpServletRequest
     * @param productId
     * @return
     */
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVo> unSelect(HttpServletRequest httpServletRequest,Integer productId){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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

        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }

    /**
     * 查询当前用户的购物车里面的产品数量
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest httpServletRequest){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

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

        if(user == null){
            return ServerResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
