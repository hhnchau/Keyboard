package phuthotech.vn.keyboard;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

public class LatinKeyboard extends Keyboard {

    static final int KEYCODE_LANGUAGE_SWITCH = -101;

    private List<Key> enterKeys;
    private Key spaceKey;

    private Key modeChangeKey;

    private Key languageSwitchKey;

    private Key savedModeChangeKey;

    private Key savedLanguageSwitchKey;

    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public LatinKeyboard(Context context, int layoutTemplateResId,
                         CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
                                   XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);
        if (key.codes[0] == 10) {
            if (enterKeys == null) {
                enterKeys = new ArrayList<Key>(2);
            }
            enterKeys.add(key);
        }
        else if (key.codes[0] == ' ') {
            spaceKey = key;
        }
        else if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
            modeChangeKey = key;
            savedModeChangeKey = new LatinKey(res, parent, x, y, parser);
        }
        else if (key.codes[0] == LatinKeyboard.KEYCODE_LANGUAGE_SWITCH) {
            languageSwitchKey = key;
            savedLanguageSwitchKey = new LatinKey(res, parent, x, y, parser);
        }
        return key;
    }

    void setLanguageSwitchKeyVisibility(boolean visible) {
        if (visible) {
            // The language switch key should be visible. Restore the size of the mode change key
            // and language switch key using the saved layout.
            modeChangeKey.width = savedModeChangeKey.width;
            modeChangeKey.x = savedModeChangeKey.x;
            languageSwitchKey.width = savedLanguageSwitchKey.width;
            languageSwitchKey.icon = savedLanguageSwitchKey.icon;
            languageSwitchKey.iconPreview = savedLanguageSwitchKey.iconPreview;
        } else {
            // The language switch key should be hidden. Change the width of the mode change key
            // to fill the space of the language key so that the user will not see any strange gap.
            modeChangeKey.width = savedModeChangeKey.width + savedLanguageSwitchKey.width;
            languageSwitchKey.width = 0;
            languageSwitchKey.icon = null;
            languageSwitchKey.iconPreview = null;
        }
    }

    void setImeOptions(Resources res, int options) {
        if (enterKeys == null) {
            return;
        }

        switch (options&(EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                updateEnterKeys(null, null, res.getText(R.string.app_name));
                break;
            case EditorInfo.IME_ACTION_NEXT:
                updateEnterKeys(null, null, res.getText(R.string.app_name));
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                updateEnterKeys(res.getDrawable(R.drawable.ic_launcher_background), null);
                break;
            case EditorInfo.IME_ACTION_SEND:
                updateEnterKeys(null, null, res.getText(R.string.app_name));
                break;
            default:
                updateEnterKeys(res.getDrawable(R.drawable.ic_launcher_background), null);
                break;
        }
    }

    private void updateEnterKeys(Drawable icon, CharSequence label) {
        for(Key enterKey : enterKeys) {
            enterKey.icon = icon;
            enterKey.label = label;
        }
    }

    private void updateEnterKeys(Drawable iconPreview, Drawable icon, CharSequence label) {
        for(Key enterKey : enterKeys) {
            enterKey.iconPreview = iconPreview;
            enterKey.icon = icon;
            enterKey.label = label;
        }
    }

    void setSpaceIcon(final Drawable icon) {
        if (spaceKey != null) {
            spaceKey.icon = icon;
        }
    }

    static class LatinKey extends Keyboard.Key {

        public LatinKey(Resources res, Keyboard.Row parent, int x, int y,
                        XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }

        @Override
        public boolean isInside(int x, int y) {
            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
        }
    }

}
