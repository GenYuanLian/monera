package com.genyuanlian.consumer.processor;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.genyuanlian.consumer.job.OrderJob;
import com.genyuanlian.consumer.service.IBWSService;
import com.genyuanlian.consumer.service.IBlockDataService;
import com.genyuanlian.consumer.service.IETHService;
import com.hnair.consumer.dao.service.ICommonService;
import com.hnair.consumer.processor.BaseApiProcessor;
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

	@Resource
	private IBlockDataService blockDataService;

	@Resource
	private IETHService ethService;

	private static final String imageDomain = ConfigPropertieUtils.getString("image.server.address");

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {

//		String url = request.getParameter("url");
//
//		// 图片上传
//		// sftp参数构造
//		Map<String, Object> sftpParams = new HashMap<>();
//		sftpParams.put("host", ConfigPropertieUtils.getString("sftp.host"));
//		sftpParams.put("port", ConfigPropertieUtils.getString("sftp.port"));
//		sftpParams.put("username", ConfigPropertieUtils.getString("sftp.username"));
//		sftpParams.put("password", ConfigPropertieUtils.getString("sftp.password"));
//		sftpParams.put("imgSize", ConfigPropertieUtils.getString("sftp.imgSize"));
//		sftpParams.put("imageSuffix", ConfigPropertieUtils.getString("sftp.imageSuffix"));
//		sftpParams.put("imageDir", ConfigPropertieUtils.getString("sftp.imageDir"));
//		sftpParams.put("basePath", ConfigPropertieUtils.getString("sftp.basePath"));
//
//		 SftpUtil.uploadLocalImage(url, sftpParams);

		// String ret = blockDataService.upload(0L, 0,
		// 0L,"根源链(SourceBlockChain)是一个领先的、基于区块链技术的以信息溯源为基础的经济协作平台，具有开放、易拓展等特性。它对经济协作过程产生的一切信息进行溯源，促进经济协作发展，达到高效率、可信任、低成本等目标");
		// BlockDataQueryResponseVo ret
		// =blockDataService.query("[\"1a1351bafb0869715533c4165fa832af39a4fd75405046430c2a81ddad5cbb80\",\"b6a78fd0f90b7f3a0e2d4a23144f8723762bbda3c045640451bf91b1e6c050fa\",\"01e30205d5bc08f3b31898c54b5432b04025cba518f44721385105f6fc1740d5\",\"dc12d2cfccdb99ad4f23d5b09d5874d49e5a7ee01fae026b10a037bacf804f4c\",\"e00637ec1cd09635f48bbe71f83c9dfe7ebe174c3a1c9d94a032579429958903\"]");
		BigDecimal ret = ethService.getBalance("0xc3f6CD622883fC71354721747EC8443498ACCf2d");
		System.out.println(JSONObject.toJSONString(ret));
	}
}
