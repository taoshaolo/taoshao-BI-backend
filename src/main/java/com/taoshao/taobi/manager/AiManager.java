package com.taoshao.taobi.manager;

import com.taoshao.taobi.common.ErrorCode;
import com.taoshao.taobi.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * （提供了通用的能力）
 * @Author taoshao
 * @Date 2024/6/25
 */
@Service
public class AiManager {

    @Resource
    private YuCongMingClient yuCongMingClient;

    /**
     * AI 对话
     * @@param modelId
     * @param message
     * @return
     */
    public String doChat(long modelId, String message) {
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);

        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if (response == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return response.getData().getContent();
    }
}
