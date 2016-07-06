/*
 * Copyright (c) 2013 Sohu TV. All rights reserved.
 */
package com.sohuvideo.playerdemo.utils;

import android.content.Intent;

/**
 * 
 * @author shileiHao 2013-5-30
 */
public class IntentUtil {

	public static boolean isEqualIntent(Intent a, Intent b) {
		if (a.filterEquals(b)) {
			if (a.getExtras() != null && b.getExtras() != null) {
				if (a.getExtras().keySet().size() != b.getExtras().keySet()
						.size())
					return false;
				for (String key : a.getExtras().keySet()) {
					if (!b.getExtras().containsKey(key)) {
						return false;
					} else if (!a.getExtras().get(key)
							.equals(b.getExtras().get(key))) {
						return false;
					}
				}
				for (String key : b.getExtras().keySet()) {
					if (!a.getExtras().containsKey(key)) {
						return false;
					} else if (!b.getExtras().get(key)
							.equals(a.getExtras().get(key))) {
						return false;
					}
				}
				return true;
			}
			if (a.getExtras() == null && b.getExtras() == null)
				return true;
			return false;
		} else {
			return false;
		}
	}
}
