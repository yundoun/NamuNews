package com.example.mynewsapp;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Fragment_01_General extends Fragment {
    private int currentPageNumber = 1; // 현재 선택된 페이지 번호를 추적하는 변수
    private List<Button> pageButtonsFirst = new ArrayList<>(); // 페이지 버튼 리스트
    private List<Button> pageButtonsSecond = new ArrayList<>(); // 페이지 버튼 리스트
    private RecyclerView recyclerView, recyclerView2;
    private NewsRvAdapter_NewsFlash adapter;
    private NewsRvAdapter_Rank adapterRank;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addPageButtons(view, R.id.page_btn_first, pageButtonsFirst); // 버튼을 추가하는 메소드 호출
        addPageButtons(view, R.id.page_btn_second, pageButtonsSecond);
    }

    // 버튼 추가 메소드에 대한 중복 제거 및 최적화
    private void addPageButtons(View rootView, int buttonsLayoutId, List<Button> pageButtons) {
        LinearLayout buttonsLayout = rootView.findViewById(buttonsLayoutId);
        buttonsLayout.removeAllViews(); // 기존에 추가된 버튼 제거
        pageButtons.clear(); // 페이지 버튼 리스트 초기화

        int widthPx = dpToPx(35); // 가로 넓이를 픽셀로 변환
        int heightPx = dpToPx(25); // 세로 넓이를 픽셀로 변환
        int margin = dpToPx(1);


        for (int i = 1; i <= 6; i++) {
            Button pageButton = createButton(String.valueOf(i), widthPx, heightPx, margin);
            final int pageNumber = i;
            pageButton.setOnClickListener(v -> {
                updatePageSelection(pageNumber, pageButtons);
            });
            buttonsLayout.addView(pageButton);
            pageButtons.add(pageButton);
        }

        updatePageSelection(1,pageButtons); // 초기 선택 상태를 1로 설정

        Button nextButton = createButton("다음", widthPx, heightPx, margin);
        nextButton.setOnClickListener(v -> {
            if (currentPageNumber < pageButtons.size()) {
                updatePageSelection(currentPageNumber + 1, pageButtons); // 다음 페이지로 이동
            } else {
                updatePageSelection(1, pageButtons); // 마지막 페이지에서 "다음"을 클릭하면 첫 페이지로 이동
            }
        });
        buttonsLayout.addView(nextButton);
    }

    private void updatePageSelection(int pageNumber, List<Button> pageButtons) {
        for (int i = 0; i < pageButtons.size(); i++) {
            Button btn = pageButtons.get(i);
            btn.setSelected(i + 1 == pageNumber);
        }
        currentPageNumber = pageNumber; // 현재 페이지 번호 업데이트
        loadPageData(pageNumber); // 페이지 데이터 로딩
    }

    // 버튼 생성 및 설정을 위한 메소드
    private Button createButton(String text, int width, int height, int margin) {
        Button button = new Button(getContext());
        button.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.page_btn)); // 배경 설정
        button.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.page_btn_selector)); // 텍스트 색상 selector 적용

        // 모든 버튼에 대해 동일한 마진 설정 적용
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(margin, 0, margin, 0); // 시작과 끝 마진 동일하게 설정
        button.setLayoutParams(params);
        button.setPadding(0, 0, 0, 0);
        button.setText(text);
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12); // 텍스트 사이즈 설정
        button.setOnClickListener(v -> {
            if ("다음".equals(text)) {
                loadNextPageGroup();
            } else {
                int pageNumber = Integer.parseInt(text);
                loadPageData(pageNumber);
            }
        });
        return button;
    }

    private void loadPageData(int page) {
        // 페이지 번호에 따른 데이터 로딩 로직 구현
        // 예: 서버에서 해당 페이지의 뉴스 데이터를 요청하고, 응답 받기
    }

    private void loadNextPageGroup() {
        // "다음" 페이지 그룹 로드하는 로직 구현
        // 예: 현재 페이지 그룹을 업데이트하고, 새로운 페이지 버튼 세트 추가
    }

    // dp를 픽셀 단위로 변환하는 헬퍼 메소드
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View firstView = inflater.inflate(R.layout.fragment_01_general, container, false);
        // Fragment에서는 findViewById를 직접 사용할 수 없고, View 인스턴스를 통해 접근해야 한다.
        // 또한, Button 인스턴스를 생성할 때 new Button(this) 대신 new Button(getContext())
        // 또는 firstView.getContext()를 사용해야 합니다.
        recyclerView = firstView.findViewById(R.id.rv_newsflash);
        recyclerView2 = firstView.findViewById(R.id.rv_rank);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

        // NewsApi 데이터 로드 메소드 호출
        Api_Manager newsApiManager = new Api_Manager();
        newsApiManager.loadNewsData(new Api_Manager.NewsDataListener() {
            @Override
            public void onDataLoaded(List<Api_NewsItem> newsItems) {
                // 데이터 로드 성공 시 어댑터에 데이터 설정
                adapter = new NewsRvAdapter_NewsFlash(getContext(), new ArrayList<>(newsItems));
                recyclerView.setAdapter(adapter);
                adapterRank = new NewsRvAdapter_Rank(getContext(), new ArrayList<>(newsItems));
                recyclerView2.setAdapter(adapterRank);
            }

            @Override
            public void onError(String errorMessage) {
                // 데이터 로드 실패 처리
                Toast.makeText(getContext(), "데이터 로드 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.d("err", errorMessage);
            }
        });

        return firstView;
    }
}
