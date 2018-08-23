package com.genyuanlian.consumer.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.genyuanlian.consumer.job.OrderJob;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.service.IBlockDataService;
import com.genyuanlian.consumer.shop.vo.BlockDataQueryResponseVo;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ErrorCode;
import com.hnair.consumer.utils.FileResponse;
import com.hnair.consumer.utils.ImageHandleUtil;
import com.hnair.consumer.utils.QrCodeCreateUtil;
import com.hnair.consumer.utils.ResultSender;
import com.hnair.consumer.utils.SftpUtil;
import com.hnair.consumer.utils.system.ConfigPropertieUtils;

@Component("hapitestCallprocessor")
public class TestCallProcessor extends BaseApiProcessor {
	@Resource
	private IBWSService bwsService;

	@Resource
	private ICommonService commonService;

	@Resource
	private OrderJob orderJob;

	@Resource
	private IBlockDataService blockDataService;

	private static final String imageDomain = ConfigPropertieUtils.getString("image.server.address");

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

		String url = request.getParameter("url");

		// 图片上传
		// sftp参数构造
		Map<String, Object> sftpParams = new HashMap<>();
		sftpParams.put("host", ConfigPropertieUtils.getString("sftp.host"));
		sftpParams.put("port", ConfigPropertieUtils.getString("sftp.port"));
		sftpParams.put("username", ConfigPropertieUtils.getString("sftp.username"));
		sftpParams.put("password", ConfigPropertieUtils.getString("sftp.password"));
		sftpParams.put("imgSize", ConfigPropertieUtils.getString("sftp.imgSize"));
		sftpParams.put("imageSuffix", ConfigPropertieUtils.getString("sftp.imageSuffix"));
		sftpParams.put("imageDir", ConfigPropertieUtils.getString("sftp.imageDir"));
		sftpParams.put("basePath", ConfigPropertieUtils.getString("sftp.basePath"));

		 SftpUtil.uploadLocalImage(url, sftpParams);

		// String ret = blockDataService.upload(0L, 0, 0L,"根源链(Source
		// BlockChain)是一个领先的、基于区块链技术的以信息溯源为基础的经济协作平台，具有开放、易拓展等特性。它对经济协作过程产生的一切信息进行溯源，促进经济协作发展，达到高效率、可信任、低成本等目标");
		// BlockDataQueryResponseVo ret =
		// blockDataService.query("[\"c3271439db492abd1adb5b71235452a58863e21e7564e481677d00158644a7a6\",\"810250b3520b2d53a1559d08e0fd6e5f89786f0fb7dd4d661cca77ce2cbb0ea9\",\"505ceb33bb9b4777d0293388a1a140f868c0674f9e25dc0aaca889bd332cc247\",\"05dec8ef7c95e6bbacfe520489857bab156d2f9a900f03d47d50d9a1cf0d910b\"]");
		// System.out.println(JSONObject.toJSONString(ret));
	}
}
