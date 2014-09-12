package com.andreapivetta.keepfocused;


public class Levels {

    public static int getTicksFromLevel(int level) {
        switch (level) {
            case 1:
                return 10;
            case 2:
                return 9;
            case 3:
                return 8;
            case 4:
                return 7;
            case 5:
                return 6;
            default:
                return 10;
        }
    }

    public static int newLevel(int currentLevel, int currentCount) {
        switch (currentLevel) {
            case 1:
                if(currentCount >= 15) return 2;
                else return currentLevel;
            case 2:
                if(currentCount >= 30) return 3;
                else return currentLevel;
            case 3:
                if(currentCount >= 50) return 4;
                else return currentLevel;
            case 4:
                if(currentCount >= 70) return 5;
                else return currentLevel;
            case 5:
                if(currentCount >= 100) return 6;
                else return currentLevel;
            default:
                return 5;
        }
    }


}
