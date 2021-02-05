# LotteryCheck
- Check my lottery number (Called 로또 in Korea)

## 개발환경
1. Java 8
2. Android 8.0(Oreo)
3. Oracle 11g

## Use API List
1. Gson 2.8.6
2. Volley 1.1.1
3. Jsoup 1.11.3
4. Zxing 3.4.0

## QR 스캔
![image](https://user-images.githubusercontent.com/65227900/106976709-3c59a880-679c-11eb-9019-68372547be17.png)
1. Zxing API를 이용, QR코드 분석
2. QR코드에 담겨있는 URL의 크롤링을 통해 자신의 번호와 회차정보 획득 후 회차번호를 토대로 Json을 통해 당첨번호와 날짜 가져온 뒤 자신의 당첨 여부를 알려줌

## 모의추첨/회원정보
![image](https://user-images.githubusercontent.com/65227900/106976935-ae31f200-679c-11eb-9d35-19b7794e3de5.png)
1. Handler를 사용하여 숫자가 실시간으로 바뀌는 연출 구현
2. DrawLayout을 사용하여 일체감있는 회원관리 UI 구현

## 기록조회/스플래쉬
![image](https://user-images.githubusercontent.com/65227900/106977454-bfc7c980-679d-11eb-9fed-fa4b83dc9491.png)
1. 리스트에서 회차 선택 후 해당 회차에 맞는 자신의 내용 출력
2. 앱 실행 시 로고를 표출하여 상징성을 증대시키는 한편 내부처리를 
