package com.lorenzovngl.domina.controllers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.lorenzovngl.domina.R;

public class ThemeController {

    public enum Color {
        LIGHT_SQUARE, DARK_SQUARE, PAWN_ONE, PAWN_TWO, BOARD_ONE_TEXT, BOARD_TWO_TEXT
    }

    public enum Drawable {
        PAWN_ONE, PAWN_TWO, CROWN_ONE, CROWN_TWO, BOARD_ONE, BOARD_TWO, PLAYER_ONE_ICON, PLAYER_TWO_ICON, MENU_GRADIENT
    }

    public static int getColorId(Context context, Color color){
        int theme = PrefsController.getTheme(context);
        int id = 0;
        switch (color){
            case LIGHT_SQUARE:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.color.lightSquareClassic : R.color.lightSquareModern;
                break;
            case DARK_SQUARE:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.color.darkSquareClassic : R.color.darkSquareModern;
                break;
            case PAWN_ONE:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.color.pawnOneClassic : R.color.pawnOneModern;
                break;
            case PAWN_TWO:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.color.pawnTwoClassic : R.color.pawnTwoModer;
                break;
            case BOARD_ONE_TEXT:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.color.colorTextDark : R.color.colorTextLight;
                break;
            case BOARD_TWO_TEXT:
                id = R.color.colorTextLight;
                break;
            default:
                break;
        }
        return id;
    }

    public static int getColor(Context context, Color color){
        return ContextCompat.getColor(context, getColorId(context, color));
    }

    public static int getDrawableId(Context context, Drawable drawable){
        int theme = PrefsController.getTheme(context);
        int id = 0;
        switch (drawable){
            case PAWN_ONE:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.drawable.circle_one_classic : R.drawable.circle_one_modern;
                break;
            case PAWN_TWO:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.drawable.circle_two_classic : R.drawable.circle_two_modern;
                break;
            case CROWN_ONE:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.drawable.crown_one_classic : R.drawable.crown_one_modern;
                break;
            case CROWN_TWO:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.drawable.crown_two_classic : R.drawable.crown_two_modern;
                break;
            case BOARD_ONE:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.drawable.rect_one_classic : R.drawable.rect_one_modern;
                break;
            case BOARD_TWO:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.drawable.rect_two_classic : R.drawable.rect_two_modern;
                break;
            case PLAYER_ONE_ICON:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.drawable.player_dark : R.drawable.player_light;
                break;
            case PLAYER_TWO_ICON:
                id = R.drawable.player_light;
                break;
            case MENU_GRADIENT:
                id = (theme == PrefsController.THEME_CLASSIC) ? R.drawable.gradient_menu_classic : R.drawable.gradient_menu_modern;
                break;
            default:
                break;
        }
        return id;
    }

    public static android.graphics.drawable.Drawable getDrawable(Context context, Drawable drawable){
        return ContextCompat.getDrawable(context, getDrawableId(context, drawable));
    }

}
