package com.smu.appmod;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

public class InstanceIDListener extends InstanceIDListenerService {
        @Override
        public void onTokenRefresh() {
             Intent intent = new Intent(this, GCMRegistrationIntentService.class);
            intent.putExtra("register",true);
            intent.putExtra("tokenRefreshed",true);
            startService(intent);
        }
}
