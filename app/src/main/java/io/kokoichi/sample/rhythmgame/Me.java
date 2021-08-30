package io.kokoichi.sample.rhythmgame;

public class Me {

    int exp, rank;

    Me() {
        exp = getExp();
        rank = 1;
    }

    int getExp() {
        // TODO:
        //  get from database
        //  Maybe use another dbHelper (DatabaseHelperMe?)
        return 3;
    }
}
