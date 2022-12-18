package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by guolin
 */

@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 创建订单
     * @param httpServletRequest
     * @param shippingId
     * @return
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest httpServletRequest, Integer shippingId){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    /**
     * 取消订单
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest httpServletRequest, Long orderNo){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }

    /**
     * 获取购物车中商品信息
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest httpServletRequest){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * 订单详情
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpServletRequest httpServletRequest,Long orderNo){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    /**
     * 个人中心查看个人订单
     * @param httpServletRequest
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }

    /**
     * 支付
     * @param httpServletRequest
     * @param orderNo 支付订单号
     * @param request
     * @return
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpServletRequest httpServletRequest, Long orderNo, HttpServletRequest request){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        // request.getSession()可以帮你得到HttpSession类型的对象
        // request.getSession().getServletContext()是获取的servlet容器对象，相当于tomcat容器
        // getRealPath(String path)给定一个URI，返回文件系统中URI对应的绝对路径（获取实际路径，项目发布时，在容器中的实际路径）
        // 注意，getRealPath(String path)里面的path默认是在webapp文件夹下
        // 并且这里返回的path最后没有 / ，如果要用 / ，则要自己拼接
        String path = request.getSession().getServletContext().getRealPath("upload");

        return iOrderService.pay(orderNo,user.getId(),path);
    }

    /**
     * 支付宝支付回调
     * @param request
     * @return
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();

        // 从request拿到参数的map
        Map requestParams = request.getParameterMap();
        // 遍历requestParams的value
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";

            // 遍历这个数组
            for(int i = 0 ; i < values.length; i++){

                valueStr = (i == values.length - 1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        // 非常重要,验证回调的正确性,是不是支付宝发的.并且呢还要避免重复通知.
        // sign_type要删掉，因为验签的时候不会验sign_type
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());

            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过,再恶意请求我就报警找网警了");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }

        // todo 验证各种数据


        // 处理alipay回调后的逻辑
        ServerResponse serverResponse = iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单支付状态
     * @param httpServletRequest
     * @param orderNo
     * @return
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest httpServletRequest, Long orderNo){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);

        // 拿到loginToken
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);

        // loginToken没拿到，用户未登录
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }

        // 通过Token拿到用户信息
        String userJsonStr = RedisPoolUtil.get(loginToken);

        // 把用户信息的字符串转成User对象
        User user = JsonUtil.string2Obj(userJsonStr,User.class);


        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
