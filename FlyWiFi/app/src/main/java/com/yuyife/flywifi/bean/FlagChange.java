package com.yuyife.flywifi.bean;

/**
 * @author yuyife Created by yuyife on 2016/6/27.
 */
public class FlagChange {
	public static int nextIndex(Boolean isEss, int fragmentFlag, int keyIndex) {
		switch (fragmentFlag) {
		case 0:
			// 1,2,3,4->list ----home
			switch (keyIndex) {
			case 1:
				keyIndex = 2;
				break;
			case 2:
				keyIndex = 3;
				break;
			case 3:
				keyIndex = 4;
				break;

			default:
				keyIndex = keyIndex + 1;
				break;
			}
			break;
		case 1:
			// 1,2,3,4,5 input
			switch (keyIndex) {
			// case 0:
			// keyIndex = 1;
			// break;
			case 1:
				if (isEss) {
					keyIndex = 4;
				} else {

					keyIndex = 2;
				}
				break;
			case 2:
				if (isEss) {
					keyIndex = 4;
				} else {

					keyIndex = 3;
				}
				break;
			case 3:

				keyIndex = 4;
				break;
			case 4:
				keyIndex = 5;
				break;
			case 5:
				break;
			}
			break;
		case 2:
			// 1,2,3,4 wpaSave
			switch (keyIndex) {
			// case 0:
			// keyIndex = 1;
			// break;
			case 1:
				keyIndex = 2;
				break;
			case 2:
				keyIndex = 3;
				break;
			case 3:
				keyIndex = 4;
				break;
			case 4:
				break;
			}
			break;
		case 3:
			// 1,2,3,4 ESSsave
			switch (keyIndex) {
			// case 0:
			// keyIndex = 1;
			// break;
			case 1:
				keyIndex = 2;
				break;
			case 2:
				keyIndex = 3;
				break;
			case 3:
				keyIndex = 4;
				break;
			case 4:
				break;
			}
			break;
		}
		return keyIndex;
	}

	public static int preIndex(Boolean isEss, int fragmentFlag, int keyIndex) {

		switch (fragmentFlag) {
		case 0:
			// 1,2,3,4->list home
			switch (keyIndex) {
			case 1:
				break;
			case 2:
				keyIndex = 1;
				break;
			case 3:
				keyIndex = 2;
				break;
			case 4:
				keyIndex = 3;
				break;
			default:
				keyIndex = keyIndex - 1;
				break;
			}

			break;
		case 1:
			// 1,2,3,4,5 input
			switch (keyIndex) {
			// case 0:
			// break;
			// case 1:
			// keyIndex = 0;
			// break;
			case 2:
				keyIndex = 1;
				break;
			case 3:
				if (isEss) {
					keyIndex = 1;
				} else {
					keyIndex = 2;
				}
				break;
			case 4:
				if (isEss) {
					keyIndex = 1;
				} else {

					keyIndex = 3;
				}
				break;
			case 5:
				keyIndex = 4;
				break;
			}
			break;
		case 2:
			// 1,2,3,4 wpaSave
			switch (keyIndex) {
			// case 0:
			// break;
			// case 1:
			// keyIndex = 0;
			// break;
			case 2:
				keyIndex = 1;
				break;
			case 3:
				keyIndex = 2;
				break;
			case 4:
				keyIndex = 3;
				break;
			}
			break;
		case 3:
			// 1,2,3,4 essSave
			switch (keyIndex) {
			// case 0:
			// break;
			// case 1:
			// keyIndex = 0;
			// break;
			case 2:
				keyIndex = 1;
				break;
			case 3:
				keyIndex = 2;
				break;
			case 4:
				keyIndex = 3;
				break;
			}
			break;
		}
		return keyIndex;
	}
}
