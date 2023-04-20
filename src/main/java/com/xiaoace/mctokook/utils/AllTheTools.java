package com.xiaoace.mctokook.utils;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;

import static com.xiaoace.mctokook.McToKook.getKbcClient;

public class AllTheTools {


    /**
     * 获取玩家的头像图标并且上传至KOOK图床并返回url
     *
     * @param playerName 玩家名字
     * @param playerUUID 玩家uuid
     * @return 上传至KOOK图床后的图片的url
     */
    @SuppressWarnings("unused")
    public static String getPlayerIconUrl(String playerName, String playerUUID){

        String playerIconUrl = UrlBuilder.of("https://crafatar.com")
                .addPath("avatars")
                .addPath(playerUUID)
                .addQuery("overlay","true").build();

        String fileName = StrUtil.format("{}.png",playerName);
        byte[] playerIconBytes = HttpUtil.downloadBytes(playerIconUrl);

        return getKbcClient().getCore().getHttpAPI().uploadFile(fileName,playerIconBytes);
    }

    public static String getPlayerIconUrl1(String playerUUID){

        return UrlBuilder.of("https://crafatar.com")
                .addPath("avatars")
                .addPath(playerUUID)
                .addQuery("overlay","true").build();
    }

}
