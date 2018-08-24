package com.smu.appmod;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

public class TermsAndConditionsActivity extends Activity implements View.OnClickListener {
    TextView tv1;
    Button close_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_conditions);
        getActionBar().setIcon(R.drawable.icon);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#08457E")));
        getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>Terms and Conditions</font>"));
        tv1 = (TextView) findViewById(R.id.tc);
        String text = "<p><b>Please read this page carefully.</b></p><p><b>1. Purpose of Research Study:</b></p><p>With an increasing usage of smartphones, safety and privacy of personal data is paramount. In this study, our goal is to understand how users of different ages interact with any malicious activity on their hand phones and how we can improve user interface to show warnings on the screen.</p><p><b>2. Study Procedures and Duration:</b></p><p>We seek participants of two different age groups: participants in their early twenties and thirties (mostly university students and young working professionals) and participants above the age of 50. We have created an app for this study which will be installed on your smart phone. This is a harmless app which will generate notifications (anytime between 8AM to 6PM) informing you about some simulated anomalous behavior e.g., an app trying to steal personal information, and then we will track what actions participants would take such as closing the app, uninstalling the app, etc. if the warnings were real. No audio or video will be recorded. These notifications are not malicious and are designed only to capture your responses to anomalous behavior. The app will be installed on your phone for one week and thereafter you can uninstall the application. You must reside in Singapore in order to participate in this study. Participation in this study is entirely voluntary. You can withdraw from the study anytime, without penalty. Please kindly inform the experimenter if you wish to withdraw from the study and then you can delete the app from your smartphone.</p><p>Participants will be asked to complete a feedback survey at the end of the study period to provide their feedback. Only the younger member of each pair of participants will need to fill in the survey form in consultation with the older member of the pair. The survey will take at most 30-40 minutes. Participants will be able to skip questions and withdraw from the survey at any time. We may contact some participants to take part in the 2nd round of the study in the future.</p><p><b>3. Benefits of Study:</b></p><p>The completion of this project would deliver a state-of-the-art system that helps end users detect anomalous behaviours and respond to them accordingly, in a seamless and intuitive way. With this system, the security of end users who access smart nation and city services through mobile applications can be better protected. You will receive $50 upon completion of the study. If you withdraw from the study before completion, you will not be paid anything. We will provide a debriefing upon completion of the study. If you are interested in knowing the research results (aggregated form), please contact the Principal Investigator and we are happy to share.</p><p><b>4. Possible Risks of Study:</b></p><p>There are no anticipated risks or adverse effects this study beyond what one would typically experience in daily life. The impact of the app on the phone’s battery life and performance would be kept minimal.</p><p><b>5. Confidentiality and Privacy of Research Data:</b></p><p>The responses will be collected anonymously and used for further analysis, and the aggregated results will be published in top tier international conferences and journals. Only the study team will have access to the responses. You would need to provide your name and acknowledge accepting the payment upon completion of the study. This information will only be used to keep track of how many people participated in the study and the total amount that was paid. This information will not be linked to your collected data.</p><p><b>6. Contact Details:</b></p><p>For questions/ clarifications on this study, please contact the Principal Investigator, David Lo, at email address davidlo@smu.edu.sg, and/or office/mobile number: +65 68280599.</p><p>If you have any questions or concerns regarding your rights as a participant in this research study and wish to contact someone unaffiliated with the research team, please contact the SMU Institutional Review Board Secretariat at irb@smu.edu.sg or + 65 68281925. When contacting SMU IRB, please provide the title of the Research Study and the name of the Principal Investigator, or quote the IRB approval number (IRB-17-021-E003(317)).</p><p>Please bookmark or save a copy of this information sheet and informed consent form for your records.</p><p><b>7. Permissions used by AppMod:</b></p><p>AppMod needs permission to access your contacts.</p><p><b>8. Consent to Participate:</b></p><p>I understand that participation is voluntary. Refusal to participate will involve no penalty.</p><p>I declare that I am at least 18 years of age.</p><p>If I am affiliated with Singapore Management University, my decision to participate, decline or withdrawal from participation will have no adverse effect on my status or future relations with Singapore Management University.</p><p>I give my consent to the Singapore Management University research team for this project to collect and use my data for the purpose(s) described in this form</p>";
        tv1.setText(Html.fromHtml(text));
        tv1.setMovementMethod(new ScrollingMovementMethod());
        close_btn = (Button) findViewById(R.id.close_btn);
        close_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_btn:
                this.finish();
                break;
        }
    }
}
