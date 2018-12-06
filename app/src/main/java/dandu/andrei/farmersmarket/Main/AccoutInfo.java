package dandu.andrei.farmersmarket.Main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import dandu.andrei.farmersmarket.R;

public class AccoutInfo extends Activity {
    @BindView(R.id.input_email) protected EditText inputEmail;
    @BindView(R.id.input_layout_email) protected TextInputLayout inputLayoutEmail;
    @BindView(R.id.input_full_name) protected EditText inputName;
    @BindView(R.id.input_layout_full_name) protected TextInputLayout inputLayoutName;
    @BindView(R.id.input_location) protected EditText inputLocation;
    @BindView(R.id.input_layout_location) protected TextInputLayout inputLayoutLocation;
    @BindView(R.id.input_street) protected EditText inputStreetName;
    @BindView(R.id.input_layout_street_name) protected TextInputLayout inputLayoutStreetName;
    @BindView(R.id.input_phone) protected EditText inputPhoneNumber;
    @BindView(R.id.input_layout_phone_number) protected TextInputLayout inputLayoutPhoneNumber;
    @BindView(R.id.input_zipcode) protected EditText inputZipcode;
    @BindView(R.id.input_layout_zipcode) protected TextInputLayout inputLayoutZipcode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_info_layout);
        ButterKnife.bind(this);
        inputEmail.setText("andreidandu@gmail.com");
        inputName.setText("Andrei");

    }
}
