package org.flexlabs.widgets.dualbattery.settings.types;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.SeekBar;
import android.widget.TextView;
import org.flexlabs.widgets.dualbattery.R;

/**
 * Created by IntelliJ IDEA.
 * User: ArtiomChi
 * Date: 12/11/11
 * Time: 23:14
 */
public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
    private static final String androidNs = "http://schemas.android.com/apk/res/android";

    private SeekBar mSeekBar;
    private TextView mValueText;
    private String mSummary;
    private int mDefault, mMin = 10, mMax, mValue = 0;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.seekbar_dialog);

        mDefault = attrs.getAttributeIntValue(androidNs, "defaultValue", 0);
        mMax = attrs.getAttributeIntValue(androidNs, "max", 100);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IntListPreference, 0, 0);
        mSummary = a.getString(R.styleable.IntListPreference_summary);
        a.recycle();
    }

    /**
     * Returns the summary of this ListPreference. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current entry
     * value will be substituted in its place.
     *
     * @return the summary with appropriate string substitution
     */
    @Override
    public CharSequence getSummary() {
        if (mSummary == null) {
            return super.getSummary();
        } else {
            return String.format(mSummary, mValue);
        }
    }

    /**
     * Sets the summary for this Preference with a CharSequence.
     * If the summary has a
     * {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current entry
     * value will be substituted in its place when it's retrieved.
     *
     * @param summary The summary for the preference.
     */
    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
        if (summary == null && mSummary != null) {
            mSummary = null;
        } else if (summary != null && !summary.equals(mSummary)) {
            mSummary = summary.toString();
        }
    }

    public void setValue(int value) {
        mValue = value;
        notifyChanged();
        persistInt(value);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mValue = getPersistedInt(mDefault);

        mValueText = (TextView)view.findViewById(R.id.value);
        mValueText.setText(String.valueOf(mValue));

        mSeekBar = (SeekBar)view.findViewById(R.id.seekbar);
        mSeekBar.setMax(mMax - mMin);
        mSeekBar.setProgress(mValue - mMin);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    public void onStartTrackingTouch(SeekBar seek) {}
    public void onStopTrackingTouch(SeekBar seek) {}
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        mValue = value + mMin;
        mValueText.setText(String.valueOf(mValue));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            if (callChangeListener(mValue))
                setValue(mValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 1);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(mValue) : (Integer) defaultValue);
    }
}
