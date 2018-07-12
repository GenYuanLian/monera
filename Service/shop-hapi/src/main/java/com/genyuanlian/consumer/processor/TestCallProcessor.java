package com.genyuanlian.consumer.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.genyuanlian.consumer.job.OrderJob;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.shop.model.ShopBstkWallet;
import com.genyuanlian.consumer.shop.vo.BWSWalletCreateResponseVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.DateUtil;
import com.hnair.consumer.utils.ImageHandleUtil;
import com.hnair.consumer.utils.ProUtility;
import com.hnair.consumer.utils.QrCodeCreateUtil;
import com.hnair.consumer.utils.ResultSender;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("hapitestCallprocessor")
public class TestCallProcessor extends BaseApiProcessor {
	@Resource
	private IBWSService bwsService;

	@Resource
	private ICommonService commonService;

	@Resource
	private OrderJob orderJob;

	private static final String imageDomain = ConfigPropertieUtils.getString("image.server.address");

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

		// 生成的二维码输出流
		ByteArrayOutputStream outputStreamQrCode = new ByteArrayOutputStream();
		QrCodeCreateUtil.writeToStream("http://www.baidu.com", 230, "png",
				imageDomain + ConfigPropertieUtils.getString("logo.url"), outputStreamQrCode);
		// 将二维码合并到背景大图后的输出流
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageHandleUtil.buildInvitationCode(
				imageDomain + ConfigPropertieUtils.getString("background.image.invitation.code"),
				new ByteArrayInputStream(outputStreamQrCode.toByteArray()),260,500, response.getOutputStream());
		
		response.setHeader("Pragma", "NO-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
	}
}
