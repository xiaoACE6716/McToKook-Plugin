package com.xiaoace.mctokook.utils;

public class MinecraftTextConverter {

    public static String convertToMinecraftFormat(String inputText) {
        String boldTextPattern = "\\*\\*(.*?)\\*\\*";
        String italicTextPattern = "\\*(.*?)\\*";
        String boldItalicTextPattern = "\\*\\*_(.*?)_\\*\\*";
        String strikethroughTextPattern = "~~(.*?)~~";
        String underlineTextPattern = "\\(ins\\)(.*?)\\(ins\\)";

        String boldReplacement = "§l$1§r";
        String italicReplacement = "§o$1§r";
        String boldItalicReplacement = "§l§o$1§r";
        String strikethroughReplacement = "§m$1§r";
        String underlineReplacement = "§n$1§r";

        return inputText.replaceAll(boldItalicTextPattern, boldItalicReplacement)
                .replaceAll(boldTextPattern, boldReplacement)
                .replaceAll(italicTextPattern, italicReplacement)
                .replaceAll(strikethroughTextPattern, strikethroughReplacement)
                .replaceAll(underlineTextPattern, underlineReplacement);
    }

}
