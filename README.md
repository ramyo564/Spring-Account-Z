# 핀테크 
1. 계좌 검색 기능
2. 계좌 관리 (생성 / 삭제 / 금액 인출 / 금액 입금)
3. 송금 기능
4. 로그인 / 로그아웃에 따른 계좌 접근 허가 기능 구현

## ERD

![account_table](https://github.com/ramyo564/Spring-Account-Z/assets/103474568/5ae269f9-de70-47be-af64-7d6451c42816)


## 1. 계좌 검색 기능
1. 거래 내역 검색 
   - 기본 -> 최근날짜순 정렬
   - 입금, 출금 순으로 정렬 기능
   - 날짜 지정 기간 거래내역 정렬
   - 보내는 사람 이나 받는 사람 조건으로 검색
   - 실패한 거래도 거래 확인 가능
  
## 2. 계좌 관리
1. 계좌 생성
    - 개인당 계좌 최대 5개 보유
2. 계좌 삭제(해지)
    - 잔액이 없을 경우에만 삭제
4. 금액 인출
    - 본인기 소유한 금액을 초과해 인출 불가
    - 음수 인출 불가
    - 계좌 소유주 확인하기
    - 해지되지 않은 계좌인지 확인
    - 잔액이 없는 상태에서 출금 불가
5. 금액 입금
    - 음수 입금 불가
    - 계좌 소유주 확인하기
    - 해지되지 않은 계좌인지 확인
      

## 3. 송금 기능
1. 전체 금액 취소
    - 거래 아이디에 해당하는 거래 확인
    - 거래와 계좌가 일치하는지 확인
    - 계좌가 존재 하는지 확인 
    - 거래금액과 거래 취소 금액이 맞는지 확인
    - 1년이 넘은 거래는 취소 불가
2. 송금 기능
    - 상대 거래 계좌 존재 여부 확인
    - 1,000,000 만원 넘는 거래일 경우 본인정보 다시 확인

## 4. 로그인 / 로그아웃
1. 계좌 접근 허가 기능 구현
    - JWT를 사용해서 로그인 및 로그아웃 구현

## Build
- Java 17
- Gradle
- H2 -> 추후 mysql로 db 변경
- JWT
- lombok
- jpa
- validation

## Trouble Shooting
