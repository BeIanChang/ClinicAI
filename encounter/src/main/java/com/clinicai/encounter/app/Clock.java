package com.clinicai.encounter.app;

import java.time.Instant;
@FunctionalInterface public interface Clock { Instant now(); }