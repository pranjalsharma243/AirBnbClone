package com.myprojects.projects.airbnb.service;

import com.myprojects.projects.airbnb.entity.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String cancelUrl);
}
