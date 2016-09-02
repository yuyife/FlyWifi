package com.flyaudio.wifi.bean;

/**
 * @author yuyife Created by yuyife on 2016/6/27.
 */
public class FlagChange {
    public static int nextIndex(int fragmentFlag, int keyIndex) {
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
                // 1,2,3,4,5 wpa
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
                        keyIndex = 5;
                        break;
                    case 5:
                        break;
                }
                break;
            case 2:
                // 1,2,3,4 open
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
                        break;
                }
                break;
            case 3:
                // 1,2,3,4 connecting
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
                        break;

                }
                break;
            case 4:
                // 1,2,3,4 already
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

    public static int preIndex(int fragmentFlag, int keyIndex) {

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
                // 1,2,3,4,5 wpa
                switch (keyIndex) {
                    // case 0:
                    // break;
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
                    case 5:
                        keyIndex = 4;
                        break;
                }
                break;
            case 2:
                // 1,2,3,4 open
                switch (keyIndex) {
                    // case 0:
                    // break;
                    case 1:
                        break;
                    case 2:
                        keyIndex = 1;
                        break;
                    case 3:
                        keyIndex = 2;
                        break;

                }
                break;
            case 3:
                // 1,2,3,4 connect
                switch (keyIndex) {
                    // case 0:
                    // break;
                    // case 1:
                    // keyIndex = 0;
                    // break;
                    case 1:
                        break;
                    case 2:
                        keyIndex = 1;
                        break;
                    case 3:
                        keyIndex = 2;
                        break;

                }
                break;
            case 4:
                // 1,2,3,4 already
                switch (keyIndex) {
                    // case 0:
                    // keyIndex = 1;
                    // break;
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
                }
                break;
        }
        return keyIndex;
    }
}
