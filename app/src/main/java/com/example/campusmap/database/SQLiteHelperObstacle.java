package com.example.campusmap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;

import com.example.campusmap.form.PointD;
import com.example.campusmap.form.Polygon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SQLiteHelperObstacle extends com.example.campusmap.database.SQLiteOpenHelper {
    private static final boolean DEBUG = true;
    private static final String DATABASE_NAME = "campus.db";
    private static SQLiteHelperObstacle instance;

    private static final int VERSION = 8;
    private static final String TABLE_NAME = "CompusOstacle";

    public static class ObstacleEntry implements BaseColumns {
        public static final String TABLE_NAME = "Polygon";
        public static final String COLUMN_NAME_BUILDING_NUMBER = "bnumber";
        public static final String COLUMN_NAME_X = "x";
        public static final String COLUMN_NAME_Y = "y";
        private static final String SQL_CREATE_TABLE =
                CREATE_TABLE_HEAD + TABLE_NAME + COLUMN_START +
                        _ID + TYPE_INTEGER + PRIMARY_KEY + COMMA_SEP +
                        COLUMN_NAME_BUILDING_NUMBER + TYPE_INTEGER + COMMA_SEP +
                        COLUMN_NAME_X + TYPE_FLOAT + COMMA_SEP +
                        COLUMN_NAME_Y + TYPE_FLOAT +
                        COLUMN_END;
        private static final String SQL_DELETE_TABLE =
                DELETE_TABLE_HEAD + IF_EXISTS + TABLE_NAME;
    }

    public static SQLiteHelperObstacle getInstance(Context context) {
        if (instance == null)
            instance = new SQLiteHelperObstacle(context);
        return instance;
    }

    private SQLiteHelperObstacle(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DEBUG)
        Log.i("DataBase Create", "데이터베이스가 생성됩니다.");
        db.execSQL(CREATE_TABLE_HEAD + TABLE_NAME+ COLUMN_START+"id INTEGER PRIMARY KEY AUTOINCREMENT, polygon TEXT NOT NULL);");
        // testing.. data..
        insert(db, "13.121 24.132,15.435 24.132,15.435 30.932,13.129 30.932,13.121 24.132");
        insert(db, "16.935 24.159,16.935 29.750,17.411 29.750,17.411 30.033,17.001 30.614,17.001 31.273,17.616 32.145,18.540 32.145,19.030 31.450,19.188 30.611,18.550 29.705,19.215 29.705,19.215 24.159,16.935 24.159");
        insert(db, "22.769 24.449,22.769 25.382,21.689 25.382,21.689 29.580,22.112 29.580,22.112 29.314,26.635 29.314,26.635 29.647,27.747 29.647,27.747 24.560,22.769 24.449");
        insert(db, "13.424 34.622,13.424 35.466,13.167 35.564,13.167 36.710,13.408 36.710,13.480 37.090,15.584 37.090,15.716 37.415,17.103 37.415,17.103 36.554,17.870 36.554,18.190 36.099,18.190 35.488,17.923 35.386,17.923 34.577,17.071 34.577,17.100 34.728,13.424 34.622");
        insert(db, "20.578 31.002,26.354 31.002,26.354 36.865,20.562 36.865,20.578 31.002");
        insert(db, "30.658 28.292,32.004 28.292,32.142 27.562,32.787 27.562,33.018 28.130,34.165 28.130,41.803 28.130,41.803 25.804,34.916 25.804,34.916 24.249,30.627 24.249,30.658 28.292");
        insert(db, "13.628 42.151,18.010 42.151,18.010 40.064,13.753 40.064,13.628 42.151");
        insert(db, "14.723 42.373,14.610 42.976,14.024 42.753,14.024 44.461,16.289 44.461,16.289 42.329,14.723 42.373");
        insert(db, "13.659 44.550,13.659 45.927,13.377 45.927,13.377 46.904,16.664 46.904,16.664 44.550,13.659 44.550");
        insert(db, "41.678 51.280,41.678 55.544,42.461 55.544,42.461 56.255,43.678 56.718,48.018 56.718,48.018 57.899,48.808 59.020,49.438 58.781,49.852 57.764,49.852 56.211,49.537 55.438,49.537 52.479,46.484 52.479,46.484 51.302,41.678 51.280");

        //
        db.execSQL( ObstacleEntry.SQL_CREATE_TABLE );

        int obstacle_id = 1;

//100주년기념관
        insertObstacle(db, obstacle_id++, 1, 128.09501883039528, 35.17973479305502);
        insertObstacle(db, obstacle_id++, 1, 128.0951713146583, 35.179440491480335);
        insertObstacle(db, obstacle_id++, 1, 128.09581823037547, 35.17958786100372);
        insertObstacle(db, obstacle_id++, 1, 128.09596764290364, 35.17947384640962);
        insertObstacle(db, obstacle_id++, 1, 128.0960946269342, 35.17952902852445);
        insertObstacle(db, obstacle_id++, 1, 128.0958211413995, 35.18000919278651);

//학생회관
        insertObstacle(db, obstacle_id++, 2, 128.09497014897877, 35.180402195823355);
        insertObstacle(db, obstacle_id++, 2, 128.09510918607285, 35.180128295190784);
        insertObstacle(db, obstacle_id++, 2, 128.09496848121663, 35.18007323610271);
        insertObstacle(db, obstacle_id++, 2, 128.09503834714394, 35.17996219506178);
        insertObstacle(db, obstacle_id++, 2, 128.09541335857602, 35.180093998860706);
        insertObstacle(db, obstacle_id++, 2, 128.09520723097515, 35.18048116915769);

//자연과학실험동(폐기)
        insertObstacle(db, obstacle_id++, 3, 128.095420318576, 35.18081722975433);
        insertObstacle(db, obstacle_id++, 3, 128.09545755731466, 35.18072901610848);
        insertObstacle(db, obstacle_id++, 3, 128.0956340582333, 35.18079276378425);
        insertObstacle(db, obstacle_id++, 3, 128.09559124068969, 35.18087426819507);

//도서관
        insertObstacle(db, obstacle_id++, 4, 128.09440945206094, 35.1807497582052);
        insertObstacle(db, obstacle_id++, 4, 128.0944735290138, 35.18061623728189);
        insertObstacle(db, obstacle_id++, 4, 128.0947298502724, 35.180697290930034);
        insertObstacle(db, obstacle_id++, 4, 128.09480240073253, 35.18058171925835);
        insertObstacle(db, obstacle_id++, 4, 128.09514719907278, 35.18071154371174);
        insertObstacle(db, obstacle_id++, 4, 128.09501066353963, 35.18096739573825);

//생명과학2호관
        insertObstacle(db, obstacle_id++, 5, 128.0950041898134, 35.18130318877676);
        insertObstacle(db, obstacle_id++, 5, 128.09509790773265, 35.18112884100533);
        insertObstacle(db, obstacle_id++, 5, 128.09575141375768, 35.181357268184435);
        insertObstacle(db, obstacle_id++, 5, 128.09566321554294, 35.18153381975951);

//박물관
        insertObstacle(db, obstacle_id++, 6, 128.09531161466683, 35.18192005293767);
        insertObstacle(db, obstacle_id++, 6, 128.09537047475513, 35.1818068580142);
        insertObstacle(db, obstacle_id++, 6, 128.09561576348977, 35.18188350301086);
        insertObstacle(db, obstacle_id++, 6, 128.0955433642679, 35.18201034003428);

//상경1호관
        insertObstacle(db, obstacle_id++, 7, 128.09469764134374, 35.18177463641092);
        insertObstacle(db, obstacle_id++, 7, 128.09474567662997, 35.18167280588541);
        insertObstacle(db, obstacle_id++, 7, 128.095126153648, 35.18180230781222);
        insertObstacle(db, obstacle_id++, 7, 128.09507537460314, 35.18190416329262);

//상경2호관
        insertObstacle(db, obstacle_id++, 8, 128.0941011617359, 35.18170566926703);
        insertObstacle(db, obstacle_id++, 8, 128.09415999317477, 35.18159022192125);
        insertObstacle(db, obstacle_id++, 8, 128.09424249904762, 35.181602995993735);
        insertObstacle(db, obstacle_id++, 8, 128.0943229210106, 35.18146031446942);
        insertObstacle(db, obstacle_id++, 8, 128.09473367274282, 35.18159630383901);
        insertObstacle(db, obstacle_id++, 8, 128.09464260705676, 35.181763867600324);
        insertObstacle(db, obstacle_id++, 8, 128.09443872357053, 35.1817048724623);
        insertObstacle(db, obstacle_id++, 8, 128.09437422290264, 35.18180685165844);

//교양학관
        insertObstacle(db, obstacle_id++, 9, 128.0946152006315, 35.181971414456804);
        insertObstacle(db, obstacle_id++, 9, 128.09466067323552, 35.181883126638);
        insertObstacle(db, obstacle_id++, 9, 128.0950272791597, 35.18200148798379);
        insertObstacle(db, obstacle_id++, 9, 128.09498183711423, 35.18209202891067);

//청담관
        insertObstacle(db, obstacle_id++, 10, 128.09498781493045, 35.1823330727551);
        insertObstacle(db, obstacle_id++, 10, 128.09507058753462, 35.18216107723541);
        insertObstacle(db, obstacle_id++, 10, 128.09551451602778, 35.18231478990945);
        insertObstacle(db, obstacle_id++, 10, 128.09542354189904, 35.182489113156784);

//자연과학1호관
        insertObstacle(db, obstacle_id++, 11, 128.09402885437777, 35.182044310236364);
        insertObstacle(db, obstacle_id++, 11, 128.09412236383162, 35.18185419233508);
        insertObstacle(db, obstacle_id++, 11, 128.09453872641947, 35.18199914465784);
        insertObstacle(db, obstacle_id++, 11, 128.09444518758121, 35.18218700988299);

//제4생활관
        insertObstacle(db, obstacle_id++, 12, 128.09450089615785, 35.1826574359682);
        insertObstacle(db, obstacle_id++, 12, 128.09462112194737, 35.18241299821865);
        insertObstacle(db, obstacle_id++, 12, 128.0953132069014, 35.18265234518556);
        insertObstacle(db, obstacle_id++, 12, 128.09519014798911, 35.18289004946485);

//제3생활관
        insertObstacle(db, obstacle_id++, 13, 128.0935448865171, 35.18217936983987);
        insertObstacle(db, obstacle_id++, 13, 128.0935820675156, 35.18208665078355);
        insertObstacle(db, obstacle_id++, 13, 128.09428506310883, 35.1823213986684);
        insertObstacle(db, obstacle_id++, 13, 128.09423431289989, 35.182425506791155);

//제1생활관
        insertObstacle(db, obstacle_id++, 14, 128.09322339697067, 35.18235577272521);
        insertObstacle(db, obstacle_id++, 14, 128.09331425500307, 35.18217243912654);
        insertObstacle(db, obstacle_id++, 14, 128.09355396412596, 35.18224237891986);
        insertObstacle(db, obstacle_id++, 14, 128.09350833922238, 35.18231940144761);
        insertObstacle(db, obstacle_id++, 14, 128.09412855802492, 35.18252109883701);
        insertObstacle(db, obstacle_id++, 14, 128.0940615839203, 35.18264337947182);

//구본관
        insertObstacle(db, obstacle_id++, 15, 128.09345663811027, 35.1809183455574);
        insertObstacle(db, obstacle_id++, 15, 128.09352599376544, 35.180769004697986);
        insertObstacle(db, obstacle_id++, 15, 128.0943998994761, 35.18106079329735);
        insertObstacle(db, obstacle_id++, 15, 128.09431405014095, 35.18120803042183);

//대학본부
        insertObstacle(db, obstacle_id++, 16, 128.0930890146361, 35.180928423934425);
        insertObstacle(db, obstacle_id++, 16, 128.09321678074437, 35.180632094635975);
        insertObstacle(db, obstacle_id++, 16, 128.09344008082297, 35.180706689337995);
        insertObstacle(db, obstacle_id++, 16, 128.09341285267152, 35.18092775388581);
        insertObstacle(db, obstacle_id++, 16, 128.09366920410753, 35.181011062755914);
        insertObstacle(db, obstacle_id++, 16, 128.09360747731395, 35.18111526976115);
//체육관
        insertObstacle(db, obstacle_id++, 17, 128.09223586205886, 35.18054630854222);
        insertObstacle(db, obstacle_id++, 17, 128.0923695711518, 35.18028372528984);
        insertObstacle(db, obstacle_id++, 17, 128.09296236065921, 35.18048792954155);
        insertObstacle(db, obstacle_id++, 17, 128.09281761633972, 35.18074610649705);

//이공1호관
        insertObstacle(db, obstacle_id++, 18, 128.09177593637193, 35.18042652666137);
        insertObstacle(db, obstacle_id++, 18, 128.09195717237856, 35.18002381371593);
        insertObstacle(db, obstacle_id++, 18, 128.09204977879247, 35.179971153900816);
        insertObstacle(db, obstacle_id++, 18, 128.09217109201307, 35.18001287176055);
        insertObstacle(db, obstacle_id++, 18, 128.0921760665543, 35.18017956760789);
        insertObstacle(db, obstacle_id++, 18, 128.09200206916958, 35.18050785825716);

//파워플랜트
        insertObstacle(db, obstacle_id++, 19, 128.0914312045917, 35.18030119844604);
        insertObstacle(db, obstacle_id++, 19, 128.09164256691366, 35.179893707945254);
        insertObstacle(db, obstacle_id++, 19, 128.09183009985475, 35.179961868285986);
        insertObstacle(db, obstacle_id++, 19, 128.09161894919248, 35.18038512996494);

//이공2호관
        insertObstacle(db, obstacle_id++, 20, 128.0917499808937, 35.17971698597004);
        insertObstacle(db, obstacle_id++, 20, 128.09180009997604, 35.17956556620479);
        insertObstacle(db, obstacle_id++, 20, 128.09228830577078, 35.179748184082555);
        insertObstacle(db, obstacle_id++, 20, 128.09220504729524, 35.17988413013337);

//생명과학1호관
        insertObstacle(db, obstacle_id++, 21, 128.09310825042533, 35.1803153656676);
        insertObstacle(db, obstacle_id++, 21, 128.09321234439508, 35.1800958605983);
        insertObstacle(db, obstacle_id++, 21, 128.09345795797836, 35.180197292898626);
        insertObstacle(db, obstacle_id++, 21, 128.09382012292735, 35.18039456194425);
        insertObstacle(db, obstacle_id++, 21, 128.09376442834895, 35.18053927309297);

//공동실험실습관
        insertObstacle(db, obstacle_id++, 22, 128.0934219507638, 35.17996778623584);
        insertObstacle(db, obstacle_id++, 22, 128.09356394774562, 35.17970963336318);
        insertObstacle(db, obstacle_id++, 22, 128.09383401594678, 35.17979281807794);
        insertObstacle(db, obstacle_id++, 22, 128.093774672125, 35.17986996462604);
        insertObstacle(db, obstacle_id++, 22, 128.0940559288163, 35.17996882055674);
        insertObstacle(db, obstacle_id++, 22, 128.0939596771393, 35.18015896321539);

//이공3호관
        insertObstacle(db, obstacle_id++, 23, 128.09263397133805, 35.17973830628536);
        insertObstacle(db, obstacle_id++, 23, 128.09270869512594, 35.179579904419626);
        insertObstacle(db, obstacle_id++, 23, 128.09309172233853, 35.17969587037783);
        insertObstacle(db, obstacle_id++, 23, 128.09316692787866, 35.179573515932695);
        insertObstacle(db, obstacle_id++, 23, 128.09332976446575, 35.179641896860886);
        insertObstacle(db, obstacle_id++, 23, 128.0931717547732, 35.17993399279296);

//기계실습장
        insertObstacle(db, obstacle_id++, 24, 128.09185597823245, 35.17943437377419);
        insertObstacle(db, obstacle_id++, 24, 128.0919309443479, 35.179293996210134);
        insertObstacle(db, obstacle_id++, 24, 128.0924024732356, 35.17946099123437);
        insertObstacle(db, obstacle_id++, 24, 128.09233583050016, 35.17960805376835);

//자동차실습장
        insertObstacle(db, obstacle_id++, 25, 128.09193631197513, 35.17928493480305);
        insertObstacle(db, obstacle_id++, 25, 128.09198700302716, 35.17917632160948);
        insertObstacle(db, obstacle_id++, 25, 128.09223514544482, 35.179261960872275);
        insertObstacle(db, obstacle_id++, 25, 128.09217359890414, 35.179379685073);

//인테리어실습장
        insertObstacle(db, obstacle_id++, 26, 128.09199502431204, 35.17916047651369);
        insertObstacle(db, obstacle_id++, 26, 128.09206727601273, 35.1790223765912);
        insertObstacle(db, obstacle_id++, 26, 128.09235567480252, 35.179040055017516);
        insertObstacle(db, obstacle_id++, 26, 128.0922651494622, 35.17924817075966);

//이공4호관
        insertObstacle(db, obstacle_id++, 27, 128.0926672021674, 35.17935044698225);
        insertObstacle(db, obstacle_id++, 27, 128.0927766636959, 35.17912188081581);
        insertObstacle(db, obstacle_id++, 27, 128.0934632271982, 35.179361288610906);
        insertObstacle(db, obstacle_id++, 27, 128.09335654156848, 35.17959208356717);
        insertObstacle(db, obstacle_id++, 27, 128.0931525135833, 35.17952182122471);
        insertObstacle(db, obstacle_id++, 27, 128.09317651650431, 35.17946977975637);
        insertObstacle(db, obstacle_id++, 27, 128.09287602382193, 35.17936884207904);
        insertObstacle(db, obstacle_id++, 27, 128.0928493972871, 35.179429920176005);

//산학협력관
        insertObstacle(db, obstacle_id++, 28, 128.0966301569825, 35.1797607720351);
        insertObstacle(db, obstacle_id++, 28, 128.09662831090944, 35.17962334038982);
        insertObstacle(db, obstacle_id++, 28, 128.09699321396846, 35.17961552921439);
        insertObstacle(db, obstacle_id++, 28, 128.0970073776827, 35.179444153785816);
        insertObstacle(db, obstacle_id++, 28, 128.09665320960764, 35.17943384177721);
        insertObstacle(db, obstacle_id++, 28, 128.09665193852825, 35.17933921670593);
        insertObstacle(db, obstacle_id++, 28, 128.09651174847139, 35.17932246002396);
        insertObstacle(db, obstacle_id++, 28, 128.09649425518938, 35.17924600783524);
        insertObstacle(db, obstacle_id++, 28, 128.0972217680001, 35.17926420477733);
        insertObstacle(db, obstacle_id++, 28, 128.09712017973573, 35.17987350305935);
        insertObstacle(db, obstacle_id++, 28, 128.0969858098757, 35.179881479980835);
        insertObstacle(db, obstacle_id++, 28, 128.0969817640238, 35.179784626804896);

//제2생활관
        insertObstacle(db, obstacle_id++, 29, 128.09591261611314, 35.178850193494256);
        insertObstacle(db, obstacle_id++, 29, 128.09592467305546, 35.1787261555149);
        insertObstacle(db, obstacle_id++, 29, 128.0964106133165, 35.17873978334126);
        insertObstacle(db, obstacle_id++, 29, 128.0964068800682, 35.178870505765346);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("DataBase Upgrade", oldVersion + "버전에서 " + newVersion + "버전으로 업그레이드를 위해 데이터베이스를 삭제합니다.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // composostacle

        // Obstacle
//        db.delete(ObstacleEntry.TABLE_NAME, "", null);
        db.execSQL(ObstacleEntry.SQL_DELETE_TABLE);
        onCreate(db);
    }

    private void insert(SQLiteDatabase db, String geomText) {
//        ContentValues values = new ContentValues();
//        values.put("polygon", geomText);
        String sql = "INSERT INTO "+TABLE_NAME+" (id, polygon) VALUES (null, '"+geomText+"')";
        Log.i("SQL line", sql);
        db.execSQL(sql);//insert(TABLE_NAME, null, values);
    }

    public Cursor select() {
        return getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public long insertObstacle(SQLiteDatabase db, int id, int number, double x, double y) {
        ContentValues values = new ContentValues();
        values.put(ObstacleEntry._ID, id);
        values.put(ObstacleEntry.COLUMN_NAME_BUILDING_NUMBER, number);
        values.put(ObstacleEntry.COLUMN_NAME_X, x);
        values.put(ObstacleEntry.COLUMN_NAME_Y, y);
        return db.insert(ObstacleEntry.TABLE_NAME, null, values);
    }

    public List<Pair<Integer, Integer>> getObstacleHeader() {
        ArrayList<Pair<Integer, Integer>> headers = new ArrayList<>();
        Cursor cursor = getReadableDatabase().query(
                ObstacleEntry.TABLE_NAME,
                new String[]{
                        ObstacleEntry.COLUMN_NAME_BUILDING_NUMBER,
                        "COUNT" + COLUMN_START + ObstacleEntry.COLUMN_NAME_BUILDING_NUMBER + COLUMN_END + " AS count_number"
                },
                null, null,
                ObstacleEntry.COLUMN_NAME_BUILDING_NUMBER, // group by
                null,
                ObstacleEntry.COLUMN_NAME_BUILDING_NUMBER + ORDER_BY_ASCENDING
        );

        while (cursor.moveToNext()) {
            headers.add(new Pair<>(
                    cursor.getInt(cursor.getColumnIndex(ObstacleEntry.COLUMN_NAME_BUILDING_NUMBER)),
                    cursor.getInt(cursor.getColumnIndex("count_number"))
            ));
        }

        cursor.close();
        return headers;
    }

    public List<Polygon> getObstacleList() {
        LinkedList<Polygon> polygons = new LinkedList<>();
        Cursor cursor = getReadableDatabase().query(
                ObstacleEntry.TABLE_NAME,
                null, null, null, null, null,
                ObstacleEntry.COLUMN_NAME_BUILDING_NUMBER + ORDER_BY_ASCENDING + COMMA_SEP + ObstacleEntry._ID + ORDER_BY_ASCENDING
        );

        int prevNumber = -1;
        while (cursor.moveToNext()) {
            int number = cursor.getInt(cursor.getColumnIndex(ObstacleEntry.COLUMN_NAME_BUILDING_NUMBER));

            if (number != prevNumber) {
                polygons.add( new Polygon(number) );
            }

            polygons.getLast().addPoint(
                    cursor.getDouble(cursor.getColumnIndex(ObstacleEntry.COLUMN_NAME_X)),
                    cursor.getDouble(cursor.getColumnIndex(ObstacleEntry.COLUMN_NAME_Y))
            );

            prevNumber = number;
        }

        cursor.close();
        return polygons;
    }

    public Pair<PointD,PointD> getMinMax() {
        Cursor cursor = getReadableDatabase().query(
                ObstacleEntry.TABLE_NAME,
                new String[]{"MIN(x) AS min_x", "MIN(y) AS min_y", "MAX(x) AS max_x", "MAX(y) AS max_y"},
                null, null,
                null, null, null
        );
        Log.i("SQLiteHelperObstacle", "getMinMax: count : " + cursor.getColumnCount());
        double min_y = cursor.getDouble(cursor.getColumnIndex("min_x"));
        double min_x = cursor.getDouble(1);
        double max_x = cursor.getDouble(3);
        double max_y = cursor.getDouble(4);
        cursor.close();

        return new Pair<>(
                new PointD(min_x, min_y),
                new PointD(max_x, max_y)
        );
    }
}
