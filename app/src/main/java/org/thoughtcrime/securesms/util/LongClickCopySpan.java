package org.thoughtcrime.securesms.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.thoughtcrime.securesms.R;

public class LongClickCopySpan extends URLSpan {

  private static final String TAG = Log.tag(LongClickCopySpan.class);

  private static final String PREFIX_MAILTO = "mailto:";
  private static final String PREFIX_TEL = "tel:";

  private boolean isHighlighted;
  @ColorInt
  private int highlightColor;

  private final Integer textColor;
  private final boolean underline;

  public LongClickCopySpan(String url) {
    this(url, null, true);
  }

  public LongClickCopySpan(String url, @ColorInt Integer textColor, boolean underline) {
    super(url);
    this.textColor = textColor;
    this.underline = underline;
  }

  void onLongClick(View widget) {
    Context context = widget.getContext();
    String preparedUrl = prepareUrl(getURL());
    copyUrl(context, preparedUrl);
    Toast.makeText(context, context.getString(R.string.ConversationItem_copied_text, preparedUrl), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void updateDrawState(@NonNull TextPaint ds) {
    super.updateDrawState(ds);
    if (textColor != null) {
      ds.setColor(textColor);
    }
    ds.bgColor = highlightColor;
    ds.setUnderlineText(!isHighlighted && underline);
  }

  void setHighlighted(boolean highlighted, @ColorInt int highlightColor) {
    this.isHighlighted = highlighted;
    this.highlightColor = highlightColor;
  }

  private void copyUrl(Context context, String url) {
    Util.writeTextToClipboard(context, url);
  }

  private String prepareUrl(String url) {
    if (url.startsWith(PREFIX_MAILTO)) {
      return url.substring(PREFIX_MAILTO.length());
    } else if (url.startsWith(PREFIX_TEL)) {
      return url.substring(PREFIX_TEL.length());
    }
    return url;
  }

  @Override public void onClick(View widget) {
    Uri uri = Uri.parse(getURL());
    if ("monero".equals(uri.getScheme())) {
      Context context = widget.getContext();
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      try {
        context.startActivity(intent);
      } catch (ActivityNotFoundException e) {
        Log.w(TAG, "Activity was not found for Monero URI");
      }
    } else {
      super.onClick(widget);
    }
  }
}
