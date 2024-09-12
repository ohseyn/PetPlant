package com.example.petplant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class GuideSlidePageFragment extends Fragment {

    private static final String ARG_PAGE_NUMBER = "page_number";
    private int pageNumber;

    // 페이지별 제목 텍스트 배열
    private String[] pageTitles = {
            "방울토마토에 대해 알아보기",
            "준비하기",
            "모종 심기",
            "지지대 세우기",
            "물주기",
            "곁순 제거하기",
            "인공 수정하기",
            "비료주기",
            "분갈이하기"
    };

    // 페이지별 설명 텍스트 배열
    private String[] pageDescriptions = {
            "방울토마토는 비타민이 풍부한 \n" +
                    "영양 만점 열매채소예요.\n" +
                    "\n" +
                    "가꾸기 쉽고 건강하게 잘 자라기 때문에\n" +
                    " 다양한 장소에서 가꿀 수 있어요.",

            "방울토마토 모종을 심기 위해서는 \n" +
                    "화분, 지지대, 끈, 모종삽, 장갑, 배양토, 물뿌리개, \n" +
                    "거름망, 방울토마토 모종이 필요해요.\n" +
                    "\n" +
                    "\n" +
                    "모종삽은 끝이 뾰족하고 날카로우므로 \n" +
                    "찔리거나 베이지 않도록 주의가 필요해요.",

            "화분에 거름망을 깔고 배양토를 채운 뒤\n" +
                    "방울토마토 모종을 심어주세요.\n" +
                    "\n" +
                    "심은 모종 위에 배양토를 덮고, \n" +
                    "물을 흠뻑 주세요.\n" +
                    "\n" +
                    "초기 방울토마토에게 가장 필요한 것은 \n" +
                    "강하고 풍부한 햇빛 또는 빛이에요.\n" +
                    "집에서 가장 햇빛이 잘 들어오는 곳에 \n" +
                    "화분을 놔주세요.",

            "방울토마토는 열매를 맺으며 \n" +
                    "위로 길게 자라는 식물이에요.\n" +
                    "\n" +
                    "식물이 쓰러지지 않도록 최대한 긴 지지대를 \n" +
                    "사용해 지지해주세요.\n" +
                    "\n" +
                    "지지대를 세울 땐 토마토의 뿌리를 자극하지 않게 \n" +
                    "조심해서 세워주세요.",

            "방울토마토 화분의 흙을 콕 찔렀을 때\n" +
                    "흙이 말라있다면 물이 필요하다는 뜻이에요.\n" +
                    "\n" +
                    "물뿌리개에 수돗물을 담아 마른 흙이\n" +
                    "남지 않도록 화분에 고루고루 물을 줘요.\n" +
                    "화분 구멍으로 물이 나올 정도로 충분히 준 뒤,\n" +
                    "화분 받침의 물을 버려주면 돼요.\n" +
                    "\n" +
                    "하루 정도 물을 담아놓아 염소 성분이 \n" +
                    "완전히 날아간 수돗물이면 더 좋아요. ",

            "곁순이 생긴다는 것은 \n" +
                    "토마토가 건강하다는 뜻이에요.\n" +
                    "\n" +
                    "하지만 곁순은 원 줄기의 영양분을 뺏어갈 수 있으니 \n" +
                    "볼 때마다 잘라서 제거해주는 게 좋아요.",

            "집에서 키우는 방울토마토는 \n" +
                    "바람, 벌 등의 도움을 받지 못해\n" +
                    "인공 수정이 필요해요.\n" +
                    "\n" +
                    "꽃이 맺힌 줄기를 톡톡 흔들어주거나\n" +
                    "꽃을 면봉이나 붓을 이용해 살살 문질러주면\n" +
                    "방울토마토가 무사히 열매를 맺을 수 있어요.",

            "방울 토마토의 열매 지름이 2-3cm가 되었을 때\n" +
                    "웃거름 또는 액체비료를 주면 좋아요.\n" +
                    "\n" +
                    "인산과 칼리가 많은 시중의 액체비료를 사용하고\n" +
                    "복합비료는 최대한 식물에게서 떨어진 곳에 주세요.",

            "화분구멍 아래로 뿌리가 빠져나와 있거나, \n" +
                    "식물의 성장이 멈췄을 때 분갈이가 필요해요.\n" +
                    "\n" +
                    "알맞은 크기의 화분에 거름망을 깔아줘요. \n" +
                    "거름망 위로 흙을 깔아 식물의 뿌리가\n" +
                    "뻗어나갈 자리를 만들어줘요.\n" +
                    "\n" +
                    "이전 화분에서 식물을 꺼내 \n" +
                    "새 화분에 자리를 잡아주세요. \n" +
                    "가장자리에 흙을 채워준 후, 물을 주면 돼요."
    };

    // 페이지별 이미지 리소스 배열
    private int[] pageImages = {
            R.drawable.guideimage1,
            R.drawable.guideimage2,
            R.drawable.guideimage3,
            R.drawable.guideimage4,
            R.drawable.guideimage5,
            R.drawable.guideimage6,
            R.drawable.guideimage7,
            R.drawable.guideimage8,
            R.drawable.guideimage9
    };

    public static GuideSlidePageFragment newInstance(int pageNumber) {
        GuideSlidePageFragment fragment = new GuideSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guide_fragment_slide_page, container, false);

        // 텍스트 및 이미지 설정
        TextView sectionTitle = view.findViewById(R.id.section_title);
        TextView sectionDescription = view.findViewById(R.id.section_description);
        ImageView tomatoImage = view.findViewById(R.id.tomato_image);

        // Java에서 텍스트 및 이미지 설정
        sectionTitle.setText(pageTitles[pageNumber]);
        sectionDescription.setText(pageDescriptions[pageNumber]);
        tomatoImage.setImageResource(pageImages[pageNumber]);

        return view;
    }
}
