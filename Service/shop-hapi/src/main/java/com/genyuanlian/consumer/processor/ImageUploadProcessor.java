package com.genyuanlian.consumer.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.genyuanlian.consumer.shop.api.IMemberApi;
import com.genyuanlian.consumer.shop.vo.ShopMessageVo;
import com.hnair.consumer.constant.ErrorCodeEnum;
import com.hnair.consumer.processor.BaseApiProcessor;
import com.hnair.consumer.utils.ResultSender;

@Component("hapiimageUploadprocessor")
public class ImageUploadProcessor extends BaseApiProcessor {
	@Resource
	private IMemberApi memberApi;

	@Override
	protected void process(HttpServletRequest request, HttpServletResponse response, ResultSender sender)
			throws Exception {
		try {
			if (!(request instanceof MultipartHttpServletRequest)) {
				sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(),
						ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(), response);
				return;
			}

			MultipartHttpServletRequest mhst = (MultipartHttpServletRequest) request;
			Map<String, MultipartFile> map = mhst.getFileMap();
			if (map.size() == 0) {
				System.out.println("获取图片内容为空######################");
				sender.fail(ErrorCodeEnum.ERROR_CODE_10002.getErrorCode(),
						ErrorCodeEnum.ERROR_CODE_10002.getErrorMessage(), response);
				return;
			}

			List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
			for (Map.Entry<String, MultipartFile> entry : map.entrySet()) {
				ShopMessageVo<Map<String, Object>> messageVo = memberApi.imageUpload(entry.getValue());
				if (messageVo.isResult()) {
					returnList.add(messageVo.getT());
				} else {
					sender.fail(Integer.parseInt(messageVo.getErrorCode()), messageVo.getErrorMessage(), response);
					return;
				}
			}

			sender.put("content", returnList);
			sender.success(response, "图片上传完成");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("上传图片出错,errorCode:" + ErrorCodeEnum.ERROR_CODE_10012.getErrorCode());
			sender.fail(ErrorCodeEnum.ERROR_CODE_10012.getErrorCode(), ErrorCodeEnum.ERROR_CODE_10012.getErrorMessage(),
					response);
			return;
		}
	}
}
