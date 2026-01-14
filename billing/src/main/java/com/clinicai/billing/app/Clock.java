package com.clinicai.billing.app;

import java.time.Instant;
@FunctionalInterface public interface Clock { Instant now(); }