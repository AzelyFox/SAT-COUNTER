package kr.devx.satcounter;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import kr.devx.satcounter.Util.FontUtil;
import kr.devx.satcounter.Util.UnivData;
import kr.devx.satcounter.Util.UserSetting;

public class SATApplication extends Application {

    public enum LOG_LEVEL {
        DEV, CORE, CRI;
    }

    public final static boolean LOG_DEV = true;
    public final static boolean LOG_CORE = true;
    public final static boolean LOG_CRI = true;
    private final static String LOG_NAME = "SAT-APP";
    private final static String LOG_STR_CON = " :: ";

    public final static int NOTIFICATION_ID = 9247;
    public final static String CHANNEL_NOTIFICATIONSERVICE = "notificationService";

    public final static String SERVER_EVENT_AVAILABLE = "http://devx.kr/files/satcounter/event.txt";

    public final HashMap<String, UnivData> KoreaUnivMap = new HashMap<>();
    public final HashMap<String, UnivData> UsUnivMap = new HashMap<>();
    public final ArrayList<String> WordList = new ArrayList<>();

    public UnivData userUniversity;
    public UserSetting userSetting;

    @Override
    public void onCreate() {
        super.onCreate();

        FontUtil.setDefaultFont(this, "DEFAULT", "NanumGothic.otf");
        FontUtil.setDefaultFont(this, "SERIF", "NanumGothic.otf");
        FontUtil.setDefaultFont(this, "MONOSPACE", "NanumGothic.otf");
        FontUtil.setDefaultFont(this, "SANS_SERIF", "NanumGothic.otf");

        MobileAds.initialize(this, "ca-app-pub-8781765548929244~4477266444");

        initializeKoreaUniv();
        initializeUsUniv();
        initializeWordList();
    }

    public static  void debugLog(LOG_LEVEL level, String header, Object log) {
        if (level == LOG_LEVEL.DEV) {
            if (LOG_DEV) Log.d(LOG_NAME, header + LOG_STR_CON + log);
            return;
        }
        if (level == LOG_LEVEL.CORE) {
            if (LOG_CORE) Log.d(LOG_NAME, header + LOG_STR_CON + log);
            return;
        }
        if (level == LOG_LEVEL.CRI) {
            if (LOG_CRI) Log.d(LOG_NAME, header + LOG_STR_CON + log);
            return;
        }
    }

    private void initializeKoreaUniv() {
        KoreaUnivMap.put("가톨릭대학교", new UnivData(this, "가톨릭대학교","나를 찾는 대학\n기쁨과 희망이 있는 대학", R.drawable.koruniv_gatholic, "#0d2e86", "#b28542"));
        KoreaUnivMap.put("건국대학교", new UnivData(this, "건국대학교", "나라를 세우고 세계를 품는 대학", R.drawable.koruniv_konkuk, "#004623"));
        KoreaUnivMap.put("경기대학교", new UnivData(this, "경기대학교", "New Start KGU!", R.drawable.koruniv_kyonggi, "#900000"));
        KoreaUnivMap.put("경희대학교", new UnivData(this, "경희대학교", "그대, 살아 숨쉰다면 경희의 이름으로 전진하라.", R.drawable.koruniv_kyunghee, "#A40E17", "#C0A355"));
        KoreaUnivMap.put("고려대학교", new UnivData(this, "고려대학교", "개척하는 지성, 개혁하는 고대", R.drawable.koruniv_korea, "#872434", "#d8ccbe"));
        KoreaUnivMap.put("광운대학교", new UnivData(this, "광운대학교", "실천궁행의 생활화", R.drawable.koruniv_kwangwoon, "#77212c", "#ffd700"));
        KoreaUnivMap.put("국민대학교", new UnivData(this, "국민대학교", "우리는 도전하는 국민인, 즉 세상을 바꾸는\n공동체적 실용융합인재를 양성할 것이다.", R.drawable.koruniv_kookmin, "#004f9f", "#a1daf8"));
        KoreaUnivMap.put("동국대학교", new UnivData(this, "동국대학교","역사를 걸으면 동국이 보이고\n동국이 걸으면 역사가 된다.\n역사를 써내려온 동국, 이젠 당신입니다.", R.drawable.koruniv_dongguk, "#ff8224"));
        KoreaUnivMap.put("명지대학교", new UnivData(this, "명지대학교","믿음의 대학, 믿을 수 있는 인재", R.drawable.koruniv_myongji, "#071648"));
        KoreaUnivMap.put("삼육대학교", new UnivData(this, "삼육대학교", "세상을 변화시키는 교육\n세상을 변화시키는 대학", R.drawable.koruniv_sahmyook, "#002c77"));
        KoreaUnivMap.put("상명대학교", new UnivData(this, "상명대학교", "가자. 내일을 향해!\n출발은 언제나 오늘에 있다.", R.drawable.koruniv_sangmyung, "#151b54"));
        KoreaUnivMap.put("서강대학교", new UnivData(this, "서강대학교", "그대 서강의 자랑이듯,\n서강 그대의 자랑이어라", R.drawable.koruniv_sogang, "#920d14"));
        KoreaUnivMap.put("서경대학교", new UnivData(this, "서경대학교", "그대 앞엔 세계, 그대 곁엔 서경", R.drawable.koruniv_seokyeong, "#48a136"));
        KoreaUnivMap.put("서울대학교", new UnivData(this, "서울대학교", "누가 조국의 미래를 묻거든\n고개를 들어 관악을 보게 하라", R.drawable.koruniv_seoulnational, "#0f0f70", "#666666"));
        KoreaUnivMap.put("서울과학기술대학교", new UnivData(this, "서울과학기술대학교", "우리의 상상이 미래를 만든다", R.drawable.koruniv_seoulst, "#023466", "#caac7a"));
        KoreaUnivMap.put("서울시립대학교", new UnivData(this, "서울시립대학교", "배움과 나눔의 100년\n서울의 자부심, 서울시립대", R.drawable.koruniv_seoulcity, "#09297f", "#dcdddd"));
        KoreaUnivMap.put("성균관대학교", new UnivData(this, "성균관대학교", "인재로서 성취되지 못한 것을 완성되게 하고\n풍속으로서 가지런하지 못한 것을 고르게 한다.", R.drawable.koruniv_sungkyunkwan, "#00693e", "#ff6c0f"));
        KoreaUnivMap.put("세종대학교", new UnivData(this, "세종대학교", "창조하라 세종처럼\nCREATIVITAS SERVITIUM", R.drawable.koruniv_sejong, "#C30E2E"));
        KoreaUnivMap.put("숭실대학교", new UnivData(this, "숭실대학교", "역사는 눈물보다 강철을 좋아했다\n그러나 우리들은 무쇠를 녹이는\n뜨거운 눈물의 역사를 만든다!", R.drawable.koruniv_soongsil, "#00b1eb", "#20b2aa"));
        KoreaUnivMap.put("연세대학교", new UnivData(this, "연세대학교", "Veritas vos liberabit\n진리가 너희를 자유롭게 하리라", R.drawable.koruniv_yonsei, "#003876", "#fdb833"));
        KoreaUnivMap.put("중앙대학교", new UnivData(this, "중앙대학교", "Live in truth. Live for justice\n의에 죽고 참에 살자", R.drawable.koruniv_chungang, "#0e5590", "#eb2a2e"));
        KoreaUnivMap.put("한국외국어대학교", new UnivData(this, "한국외국어대학교", "외대를 만나면 세계가 보인다", R.drawable.koruniv_hankukforeign, "#002d56", "#8d7150"));
        KoreaUnivMap.put("한성대학교", new UnivData(this, "한성대학교", "서울의 센터 한성대학교\n상상력 이노베이터", R.drawable.koruniv_hansung, "#004f9f"));
        KoreaUnivMap.put("한양대학교", new UnivData(this, "한양대학교", "The Engine of Korea\nHanyang University", R.drawable.koruniv_hanyang, "#00457e"));
        KoreaUnivMap.put("홍익대학교", new UnivData(this, "홍익대학교", "弘益人間\n널리 사람을 이롭게 하라", R.drawable.koruniv_hongik, "#002c62", "#d8ccbe"));
        KoreaUnivMap.put("부산대학교", new UnivData(this, "부산대학교", "학생의 미래가 있는 대학\n국민으로부터 사랑받는 대학", R.drawable.koruniv_pusan, "#005baa", "#00a651"));
        KoreaUnivMap.put("경북대학교", new UnivData(this, "경북대학교", "미래를 주도하는 첨성인\n세상을 선도하는 경북대", R.drawable.koruniv_kyungpook, "#e60000", "#bf7c26"));
        KoreaUnivMap.put("대구교육대학교", new UnivData(this, "대구교육대학교", "슬기,보람,사랑", R.drawable.koruniv_daeguedu, "#008dd0", "#20b471"));
        KoreaUnivMap.put("가천대학교", new UnivData(this, "가천대학교", "아름다운(嘉) 인재의 샘(泉)", R.drawable.koruniv_gachon, "#003399", "#ffa500"));
        KoreaUnivMap.put("인천대학교", new UnivData(this, "인천대학교", "INspiring U\n당신을 깨우는 대학", R.drawable.koruniv_incheon,"#094a9a", "#fdaf17"));
        KoreaUnivMap.put("인하대학교", new UnivData(this, "인하대학교","INHA, Innovation Begins Here!", R.drawable.koruniv_inha, "#0b419b", "#2499c4"));
        KoreaUnivMap.put("전북대학교", new UnivData(this, "전북대학교", "성장을 넘어 성숙으로\n천년의 웅비 전북대학교", R.drawable.koruniv_chonbuk, "#56296e", "#c9c8c5"));
        KoreaUnivMap.put("전남대학교", new UnivData(this, "전남대학교", "진리로 행복한 세상을 밝힌다.", R.drawable.koruniv_chonnam, "#009040", "#fcce33"));
        KoreaUnivMap.put("충북대학교", new UnivData(this, "충북대학교", "NOVA APERIO\n개신(開新)", R.drawable.koruniv_chungbuk, "#7d2248", "#8c8279"));
        KoreaUnivMap.put("충남대학교", new UnivData(this, "충남대학교", "세계로 도약하는 대한민국 대표대학", R.drawable.koruniv_chungnam, "#01499D"));
        KoreaUnivMap.put("단국대학교", new UnivData(this, "단국대학교", "독립은 남이 갖다 주는 것이 아니고\n오직 우리 자신의 힘으로 쟁취해야 한다", R.drawable.koruniv_dankook, "#00529c"));
        KoreaUnivMap.put("한국항공대학교", new UnivData(this, "한국항공대학교", "세계를 향한, 미래를 향한 도전", R.drawable.koruniv_koreaaerospace, "#262c88", "#3e3580"));
        KoreaUnivMap.put("서울교육대학교", new UnivData(this, "서울교육대학교", "내 힘으로 한 마음으로", R.drawable.koruniv_seouledu, "#0f0f70"));
        KoreaUnivMap.put("KAIST", new UnivData(this, "한국과학기술원", "세계의 중심에서 세상을 바꾼다", R.drawable.koruniv_kaist, "#01438f", "#2088ca"));
        KoreaUnivMap.put("카이스트", new UnivData(this, "한국과학기술원", "세계의 중심에서 세상을 바꾼다", R.drawable.koruniv_kaist, "#01438f", "#2088ca"));
        KoreaUnivMap.put("부산교육대학교", new UnivData(this, "부산교육대학교", "큰 사랑, 빛난 슬기, 알찬 봉사!", R.drawable.koruniv_busanedu, "#4375db"));
        KoreaUnivMap.put("DGIST", new UnivData(this, "대구경북과학기술원", "세계 초일류 융복합 대학", R.drawable.koruniv_dgist, "#004890", "#77787c"));
        KoreaUnivMap.put("대구경북과학기술원", new UnivData(this, "대구경북과학기술원", "세계 초일류 융복합 대학", R.drawable.koruniv_dgist, "#004890", "#77787c"));
        KoreaUnivMap.put("경인교육대학교", new UnivData(this, "경인교육대학교", "큰 힘, 큰 사랑, 큰 빛", R.drawable.koruniv_gyeonginedu, "#80254e", "#c07700"));
        KoreaUnivMap.put("GIST", new UnivData(this, "광주과학기술원", "미래를 향한 창의적 과학기술의 요람", R.drawable.koruniv_gist, "#df3327", "#546461"));
        KoreaUnivMap.put("광주과학기술원", new UnivData(this, "광주과학기술원", "미래를 향한 창의적 과학기술의 요람", R.drawable.koruniv_gist, "#df3327", "#546461"));
        KoreaUnivMap.put("UNIST", new UnivData(this, "울산과학기술원", "FIRST IN CHANGE", R.drawable.koruniv_unist, "#001c54", "#43c1c3"));
        KoreaUnivMap.put("울산과학기술원", new UnivData(this, "울산과학기술원", "FIRST IN CHANGE", R.drawable.koruniv_unist, "#001c54", "#43c1c3"));
        KoreaUnivMap.put("덕성여자대학교", new UnivData(this, "덕성여자대학교", "Double Synergy", R.drawable.koruniv_duksung, "#ac145a"));
        KoreaUnivMap.put("숙명여자대학교", new UnivData(this, "숙명여자대학교", "세상을 바꾸는 부드러운 힘", R.drawable.koruniv_sookmyung, "#0d2d84"));
        KoreaUnivMap.put("이화여자대학교", new UnivData(this, "이화여자대학교", "세상은 이화에게 물었고\n이화는 그대를 답했다", R.drawable.koruniv_ewha, "#006640", "#dedbc4"));
        KoreaUnivMap.put("경찰대학", new UnivData(this, "경찰대학", "이곳을 거쳐가는 이여\n조국은 그대를 믿노라", R.drawable.koruniv_police, "#A4681D", "#FFCB08"));
        KoreaUnivMap.put("육군사관학교", new UnivData(this, "육군사관학교", "70년 호국전통\n통일한국 주역으로", R.drawable.koruniv_military, "#005801", "#FAE723"));
        KoreaUnivMap.put("해군사관학교", new UnivData(this, "해군사관학교", "진리를 구하자, 허위를 버리자, 희생하자", R.drawable.koruniv_naval, "#182689", "#FBD51A"));
        KoreaUnivMap.put("공군사관학교", new UnivData(this, "공군사관학교", "배우고 익혀서 몸과 마음을\n조국과 하늘에 바친다", R.drawable.koruniv_airforce, "#0C4EA1"));
        KoreaUnivMap.put("한국교통대학교", new UnivData(this, "한국교통대학교", "CONNECT THE WORLD\n세상에 통하는 대학", R.drawable.koruniv_transportaion, "#590000", "#0155a1"));
        KoreaUnivMap.put("부경대학교", new UnivData(this, "부경대학교", "미래를 우리 손으로", R.drawable.koruniv_pukyong, "#13007c", "#0480b8"));
        initializeKoreaUnivAdditional();
    }

    private void initializeKoreaUnivAdditional() {
        KoreaUnivMap.put("Princeton University", new UnivData(this, "Princeton University", "Dei Sub Numine Viget", R.drawable.usuniv_princeton, "#f46f1b", "#231f20"));
        KoreaUnivMap.put("프린스턴 대학교", new UnivData(this, "Princeton University", "Dei Sub Numine Viget", R.drawable.usuniv_princeton, "#f46f1b", "#231f20"));
        KoreaUnivMap.put("Harvard University", new UnivData(this, "Harvard University", "Veritas", R.drawable.usuniv_harvard, "#c90016", "#ad8b3a"));
        KoreaUnivMap.put("하버드 대학교", new UnivData(this, "Harvard University", "Veritas", R.drawable.usuniv_harvard, "#c90016", "#ad8b3a"));
        KoreaUnivMap.put("Columbia University", new UnivData(this, "Columbia University", "In lumine Tuo videbimus lumen", R.drawable.usuniv_columbia, "#0058a9", "#ace1fa"));
        KoreaUnivMap.put("컬럼비아 대학교", new UnivData(this, "Columbia University", "In lumine Tuo videbimus lumen", R.drawable.usuniv_columbia, "#0058a9", "#ace1fa"));
        KoreaUnivMap.put("Massachusetts Institute of Technology", new UnivData(this, "MIT", "Mens et Manus", R.drawable.usuniv_mit, "#a31f34", "#8a8b8c"));
        KoreaUnivMap.put("메사추세츠 공과대학교", new UnivData(this, "MIT", "Mens et Manus", R.drawable.usuniv_mit, "#a31f34", "#8a8b8c"));
        KoreaUnivMap.put("MIT", new UnivData(this, "MIT", "Mens et Manus", R.drawable.usuniv_mit, "#a31f34", "#8a8b8c"));
        KoreaUnivMap.put("University of Chicago", new UnivData(this, "University of Chicago", "Crescat scientia\nvita excolatur", R.drawable.usuniv_chicago, "#880a22"));
        KoreaUnivMap.put("시카고 대학교", new UnivData(this, "University of Chicago", "Crescat scientia\nvita excolatur", R.drawable.usuniv_chicago, "#880a22"));
        KoreaUnivMap.put("Yale University", new UnivData(this, "Yale University", "Lux et veritas\nאורים ותמים", R.drawable.usuniv_yale, "#00356b"));
        KoreaUnivMap.put("예일 대학교", new UnivData(this, "Yale University", "Lux et veritas\nאורים ותמים", R.drawable.usuniv_yale, "#00356b"));
        KoreaUnivMap.put("Stanford University", new UnivData(this, "Stanford University", "The wind of freedom blows", R.drawable.usuniv_stanford, "#942525"));
        KoreaUnivMap.put("스탠퍼드 대학교", new UnivData(this, "Stanford University", "The wind of freedom blows", R.drawable.usuniv_stanford, "#942525"));
        KoreaUnivMap.put("University of Pennsylvania", new UnivData(this, "University of Pennsylvania", "Leges sine moribus vanae", R.drawable.usuniv_pennsylvania, "#191970", "#990000"));
        KoreaUnivMap.put("펜실베니아 대학교", new UnivData(this, "University of Pennsylvania", "Leges sine moribus vanae", R.drawable.usuniv_pennsylvania, "#191970", "#990000"));
        KoreaUnivMap.put("Duke University", new UnivData(this, "Duke University", "Eruditio et Religio", R.drawable.usuniv_duke, "#00009c"));
        KoreaUnivMap.put("듀크 대학교", new UnivData(this, "Duke University", "Eruditio et Religio", R.drawable.usuniv_duke, "#00009c"));
        KoreaUnivMap.put("Johns Hopkins University", new UnivData(this, "Johns Hopkins University", "Veritas vos liberabit", R.drawable.usuniv_johnshopkins, "#0000ff"));
        KoreaUnivMap.put("존스 홉킨스 대학교", new UnivData(this, "Johns Hopkins University", "Veritas vos liberabit", R.drawable.usuniv_johnshopkins, "#0000ff"));
        KoreaUnivMap.put("Northwestern University", new UnivData(this, "Northwestern University", "Quaecumque sunt vera", R.drawable.usuniv_northwestern, "#57068c"));
        KoreaUnivMap.put("노스웨스턴 대학교", new UnivData(this, "Northwestern University", "Quaecumque sunt vera", R.drawable.usuniv_northwestern, "#57068c"));
        KoreaUnivMap.put("California Institute of Technology", new UnivData(this, "Caltech", "The truth shall make you free", R.drawable.usuniv_caltech, "#f37421"));
        KoreaUnivMap.put("캘리포니아 공과대학교", new UnivData(this, "Caltech", "The truth shall make you free", R.drawable.usuniv_caltech, "#f37421"));
        KoreaUnivMap.put("Dartmouth College", new UnivData(this, "Dartmouth College", "Vox clamantis in deserto", R.drawable.usuniv_dartmouth, "#387852"));
        KoreaUnivMap.put("다트머스 대학교", new UnivData(this, "Dartmouth College", "Vox clamantis in deserto", R.drawable.usuniv_dartmouth, "#387852"));
        KoreaUnivMap.put("Brown University", new UnivData(this, "Brown University", "In Deo speramus", R.drawable.usuniv_brown, "#381c00", "#cc0000"));
        KoreaUnivMap.put("브라운 대학교", new UnivData(this, "Brown University", "In Deo speramus", R.drawable.usuniv_brown, "#381c00", "#cc0000"));
        KoreaUnivMap.put("Vanderbilt University", new UnivData(this, "Vanderbilt University", "We are Vanderbilt", R.drawable.usuniv_vanderbilt, "#000000", "#ffd700"));
        KoreaUnivMap.put("밴더빌트 대학교", new UnivData(this, "Vanderbilt University", "We are Vanderbilt", R.drawable.usuniv_vanderbilt, "#000000", "#ffd700"));
        KoreaUnivMap.put("Cornell University", new UnivData(this, "Cornell University", "any person can find instruction in any study.", R.drawable.usuniv_cornell, "#9b2929"));
        KoreaUnivMap.put("코넬 대학교", new UnivData(this, "Cornell University", "any person can find instruction in any study.", R.drawable.usuniv_cornell, "#9b2929"));
        KoreaUnivMap.put("Rice University", new UnivData(this, "Rice University", "Letters, Science, Art", R.drawable.usuniv_rice, "#000080"));
        KoreaUnivMap.put("라이스 대학교", new UnivData(this, "Rice University", "Letters, Science, Art", R.drawable.usuniv_rice, "#000080"));
        KoreaUnivMap.put("University of Notre Dame du Lac", new UnivData(this, "University of Notre Dame", "Vita, Dulcedo, Spes", R.drawable.usuniv_notredame, "#dbc35a", "#000080"));
        KoreaUnivMap.put("노트르담 대학교", new UnivData(this, "University of Notre Dame", "Vita, Dulcedo, Spes", R.drawable.usuniv_notredame, "#dbc35a", "#000080"));
        KoreaUnivMap.put("Washington University in St. Louis", new UnivData(this, "Washington University", "Per veritatem vis", R.drawable.usuniv_washington, "#a60c10", "#ff0000"));
        KoreaUnivMap.put("세인트루이스 워싱턴 대학교", new UnivData(this, "Washington University", "Per veritatem vis", R.drawable.usuniv_washington, "#a60c10", "#ff0000"));
        KoreaUnivMap.put("University of California, Los Angeles", new UnivData(this, "UCLA", "Let there be light", R.drawable.usuniv_ucla, "#0000ff", "#ffd700"));
        KoreaUnivMap.put("UCLA", new UnivData(this, "UCLA", "Let there be light", R.drawable.usuniv_ucla, "#0000ff", "#ffd700"));
        KoreaUnivMap.put("Emory University", new UnivData(this, "Emory University", "Cor prudentis possidebit scientiam", R.drawable.usuniv_emory, "#155899"));
        KoreaUnivMap.put("에모리 대학교", new UnivData(this, "Emory University", "Cor prudentis possidebit scientiam", R.drawable.usuniv_emory, "#155899"));
        KoreaUnivMap.put("University of California, Berkeley", new UnivData(this, "UC Berkeley", "Let there be light", R.drawable.usuniv_berkeley, "#d19000", "#003a70"));
        KoreaUnivMap.put("UC 버클리", new UnivData(this, "UC Berkeley", "Let there be light", R.drawable.usuniv_berkeley, "#d19000", "#003a70"));
        KoreaUnivMap.put("Georgetown University", new UnivData(this, "Georgetown University", "Utraue Unum", R.drawable.usuniv_georgetown, "#011e41"));
        KoreaUnivMap.put("조지타운 대학교", new UnivData(this, "Georgetown University", "Utraue Unum", R.drawable.usuniv_georgetown, "#011e41"));
        KoreaUnivMap.put("University of Southern California", new UnivData(this, "USC", "Palmam qui meruit ferat", R.drawable.usuniv_southerncalifornia, "#9c0707"));
        KoreaUnivMap.put("서던 캘리포니아 대학교", new UnivData(this, "USC", "Palmam qui meruit ferat", R.drawable.usuniv_southerncalifornia, "#9c0707"));
        KoreaUnivMap.put("Carnegie Mellon University", new UnivData(this, "Carnegie Mellon University", "My heart is in the work", R.drawable.usuniv_carnegiemellon, "#ab1727"));
        KoreaUnivMap.put("카네기 멜런 대학교", new UnivData(this, "Carnegie Mellon University", "My heart is in the work", R.drawable.usuniv_carnegiemellon, "#ab1727"));
        KoreaUnivMap.put("University of Virginia", new UnivData(this, "University of Virginia", "Here was buried Thomas Jefferson", R.drawable.usuniv_verginia, "#1e4b87", "#c37930"));
        KoreaUnivMap.put("버지니아 대학교", new UnivData(this, "University of Virginia", "Here was buried Thomas Jefferson", R.drawable.usuniv_verginia, "#1e4b87", "#c37930"));
        KoreaUnivMap.put("Tufts University", new UnivData(this, "Tufts University", "Pax et Lux", R.drawable.usuniv_tufts, "#418fde"));
        KoreaUnivMap.put("터프츠 대학교", new UnivData(this, "Tufts University", "Pax et Lux", R.drawable.usuniv_tufts, "#418fde"));
        KoreaUnivMap.put("University of Michigan", new UnivData(this, "University of Michigan", "Artes, Scientia, Veritas", R.drawable.usuniv_michigan, "#00274c", "#ffcb05"));
        KoreaUnivMap.put("미시간 대학교", new UnivData(this, "University of Michigan", "Artes, Scientia, Veritas", R.drawable.usuniv_michigan, "#00274c", "#ffcb05"));
        KoreaUnivMap.put("Wake Forest University", new UnivData(this, "Wake Forest University", "Pro humanitate", R.drawable.usuniv_wakeforest, "#000000", "#9e7e38"));
        KoreaUnivMap.put("웨이크 포레스트 대학교", new UnivData(this, "Wake Forest University", "Pro humanitate", R.drawable.usuniv_wakeforest, "#000000", "#9e7e38"));
        KoreaUnivMap.put("University of North Carolina", new UnivData(this, "University of North Carolina", "Lux, Libertas", R.drawable.usuniv_northcarolina, "#7bafd4"));
        KoreaUnivMap.put("노스 캐롤라이나 대학교", new UnivData(this, "University of North Carolina", "Lux, Libertas", R.drawable.usuniv_northcarolina, "#7bafd4"));
        KoreaUnivMap.put("NewYork University", new UnivData(this, "NewYork University", "Perstare et praestare", R.drawable.usuniv_newyork, "#7bafd4"));
        KoreaUnivMap.put("뉴욕 대학교", new UnivData(this, "NewYork University", "Perstare et praestare", R.drawable.usuniv_newyork, "#7bafd4"));
    }

    private void initializeUsUniv() {
        UsUnivMap.put("Princeton University", new UnivData(this, "Princeton University", "Dei Sub Numine Viget", R.drawable.usuniv_princeton, "#f46f1b", "#231f20"));
        UsUnivMap.put("Harvard University", new UnivData(this, "Harvard University", "Veritas", R.drawable.usuniv_harvard, "#c90016", "#ad8b3a"));
        UsUnivMap.put("Columbia University", new UnivData(this, "Columbia University", "In lumine Tuo videbimus lumen", R.drawable.usuniv_columbia, "#0058a9", "#ace1fa"));
        UsUnivMap.put("Massachusetts Institute of Technology", new UnivData(this, "MIT", "Mens et Manus", R.drawable.usuniv_mit, "#a31f34", "#8a8b8c"));
        UsUnivMap.put("MIT", new UnivData(this, "MIT", "Mens et Manus", R.drawable.usuniv_mit, "#a31f34", "#8a8b8c"));
        UsUnivMap.put("University of Chicago", new UnivData(this, "University of Chicago", "Crescat scientia\nvita excolatur", R.drawable.usuniv_chicago, "#880a22"));
        UsUnivMap.put("Yale University", new UnivData(this, "Yale University", "Lux et veritas\nאורים ותמים", R.drawable.usuniv_yale, "#00356b"));
        UsUnivMap.put("Stanford University", new UnivData(this, "Stanford University", "The wind of freedom blows", R.drawable.usuniv_stanford, "#942525"));
        UsUnivMap.put("University of Pennsylvania", new UnivData(this, "University of Pennsylvania", "Leges sine moribus vanae", R.drawable.usuniv_pennsylvania, "#191970", "#990000"));
        UsUnivMap.put("Duke University", new UnivData(this, "Duke University", "Eruditio et Religio", R.drawable.usuniv_duke, "#00009c"));
        UsUnivMap.put("Johns Hopkins University", new UnivData(this, "Johns Hopkins University", "Veritas vos liberabit", R.drawable.usuniv_johnshopkins, "#0000ff"));
        UsUnivMap.put("Northwestern University", new UnivData(this, "Northwestern University", "Quaecumque sunt vera", R.drawable.usuniv_northwestern, "#57068c"));
        UsUnivMap.put("California Institute of Technology", new UnivData(this, "Caltech", "The truth shall make you free", R.drawable.usuniv_caltech, "#f37421"));
        UsUnivMap.put("Dartmouth College", new UnivData(this, "Dartmouth College", "Vox clamantis in deserto", R.drawable.usuniv_dartmouth, "#387852"));
        UsUnivMap.put("Brown University", new UnivData(this, "Brown University", "In Deo speramus", R.drawable.usuniv_brown, "#381c00", "#cc0000"));
        UsUnivMap.put("Vanderbilt University", new UnivData(this, "Vanderbilt University", "We are Vanderbilt", R.drawable.usuniv_vanderbilt, "#000000", "#ffd700"));
        UsUnivMap.put("Cornell University", new UnivData(this, "Cornell University", "any person can find instruction in any study.", R.drawable.usuniv_cornell, "#9b2929"));
        UsUnivMap.put("Rice University", new UnivData(this, "Rice University", "Letters, Science, Art", R.drawable.usuniv_rice, "#000080"));
        UsUnivMap.put("University of Notre Dame du Lac", new UnivData(this, "University of Notre Dame", "Vita, Dulcedo, Spes", R.drawable.usuniv_notredame, "#dbc35a", "#000080"));
        UsUnivMap.put("Washington University in St. Louis", new UnivData(this, "Washington University", "Per veritatem vis", R.drawable.usuniv_washington, "#a60c10", "#ff0000"));
        UsUnivMap.put("University of California, Los Angeles", new UnivData(this, "UCLA", "Let there be light", R.drawable.usuniv_ucla, "#0000ff", "#ffd700"));
        UsUnivMap.put("Emory University", new UnivData(this, "Emory University", "Cor prudentis possidebit scientiam", R.drawable.usuniv_emory, "#155899"));
        UsUnivMap.put("University of California, Berkeley", new UnivData(this, "UC Berkeley", "Let there be light", R.drawable.usuniv_berkeley, "#d19000", "#003a70"));
        UsUnivMap.put("Georgetown University", new UnivData(this, "Georgetown University", "Utraue Unum", R.drawable.usuniv_georgetown, "#011e41"));
        UsUnivMap.put("University of Southern California", new UnivData(this, "USC", "Palmam qui meruit ferat", R.drawable.usuniv_southerncalifornia, "#9c0707"));
        UsUnivMap.put("Carnegie Mellon University", new UnivData(this, "Carnegie Mellon University", "My heart is in the work", R.drawable.usuniv_carnegiemellon, "#ab1727"));
        UsUnivMap.put("University of Virginia", new UnivData(this, "University of Virginia", "Here was buried Thomas Jefferson", R.drawable.usuniv_verginia, "#1e4b87", "#c37930"));
        UsUnivMap.put("Tufts University", new UnivData(this, "Tufts University", "Pax et Lux", R.drawable.usuniv_tufts, "#418fde"));
        UsUnivMap.put("University of Michigan", new UnivData(this, "University of Michigan", "Artes, Scientia, Veritas", R.drawable.usuniv_michigan, "#00274c", "#ffcb05"));
        UsUnivMap.put("Wake Forest University", new UnivData(this, "Wake Forest University", "Pro humanitate", R.drawable.usuniv_wakeforest, "#000000", "#9e7e38"));
        UsUnivMap.put("University of North Carolina", new UnivData(this, "University of North Carolina", "Lux, Libertas", R.drawable.usuniv_northcarolina, "#7bafd4"));
        UsUnivMap.put("NewYork University", new UnivData(this, "NewYork University", "Perstare et praestare", R.drawable.usuniv_newyork, "#7bafd4"));
    }

    private void initializeWordList() {
        WordList.addAll(Arrays.asList(getResources().getStringArray(R.array.word_list)));
    }

    public String randomFromWordList() {
        Random generator = new Random();
        return WordList.get(generator.nextInt(WordList.size() - 1));
    }

    public static String getUserInfo() {
        String version = String.valueOf(Build.VERSION.SDK_INT);
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return version + " " + capitalize(model);
        } else {
            return version + " " + capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

}
