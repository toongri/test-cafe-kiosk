# 개요
테스트 코드 작성은 결국 추가작업입니다.  
우리는 왜 추가적으로 기능을 개발하지 않고 테스트코드를 작성해야할까요?
그리고 어떻게 작성해야할까요?

- 테스트 코드는 피드백이 빠릅니다.  
기능은 빠르게 변화하고 확장해갑니다. 
기능이 변화하거나 추가될 때마다 우리는 테스트를 진행해야합니다.  
테스트 코드를 작성해놓음으로써 기존의 영역들은 빠르게 피드백 받을 수 있게 됩니다.
- 테스트 코드는 테스트의 자동화입니다.  
테스트 코드를 작성해놓지 않으면 해당 기능이 변화할 때마다 같은 수동 테스트를 매번 반복해야합니다.
- 스펙 공유  
테스트 코드를 잘 작성해놓음으로써 해당 기능에는 어떠한 테스트가 필요한지 명시해놓을 수 있습니다.
- 안정감  
테스트 코드를 작 작성함으로써 코드의 안정성이 올라가며, 프로덕트 퀄리티가 상승할 수 있습니다.

# 수동 테스트

테스트 결과를 콘솔에 찍어가며 테스트를 진행하는 것을 수동 테스트라고 합니다.
수동 테스트는 콘솔에 결과값을 찍으면 사람이 직접 테스트 성공 여부를 판단합니다.

다음과 같은 테스트는 개선점이 몇가지 있습니다.

- 테스트가 어떤 것을 테스트하려는지 알기 쉽지 않습니다.
- 모든 개발자는 해당 테스트에 대한 스펙과 예상 결과값을 전부 알고 있어야합니다.

이것을 개선하기 위해 테스트 자동화 라이브러리가 등장했습니다.

# 테스트 자동화

## 테스트 케이스 세분화
요구사항이 들어왔을 때, 암묵적거나 누락된 요구사항이 없는지 고민하고 재질문 할 수 있어야 합니다.

테스트를 작성할 때 두가지 케이스에 대해 테스트해볼 수 있습니다.

- 해피 케이스  
기능의 스펙대로 잘 동작하는 케이스입니다.
- 예외 케이스  
해피 케이스 외에 발생할 수 있는 예외적인 케이스입니다.

## 경계값 테스트
경계값 테스트는 테스트 케이스를 세분화할 때 꼭 고려해야하는 부분입니다.

경계값 테스트란 범위, 구간, 날짜 등 로직상 허용되는 경계에 해당하는 값을 테스트하는 것을 말합니다.

### 예시
#### 요구사항
요구사항은 다음과 같습니다.
> 음료를 주문할 때 음료의 수량은 1잔 이상 주문할 수 있다.

#### 코드 작성
다음과 같은 요구사항을 보고 테스트를 작성하였습니다.

```java

    /**
     * Given: 키오스크가 존재하고
     * Given: 메뉴에 아메리카노가 존재하다면
     * When: 음료 0잔을 키오스크에 추가할 때
     * Then: IllegalArgumentException 이 발생한다.
     */
    @Test
    void addZeroBeverages() {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        //when
        Throwable throwable = catchThrowable(() -> cafeKiosk.add(americano, 0));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("음료는 1잔 이상 주문하실 수 있습니다.");

    }

```

## 테스트에 용이한 코드

테스트에 용이한 코드가 되기 위해 우리는 테스트하기 어려운 영역을 분리해야합니다.

### 외부 영역 분리
테스트하기 어려운 영역은 다음과 같습니다.
- 외부 세계에 영향을 받는 코드
- 외부 세계에 영향을 주는 코드

외부 세계란 다음과 같은 것들을 말합니다.
- 현재 날짜
- 랜덤값
- 전역 변수/함수
- 외부 API
- 사용자 입력
- 표준 출력
- 메세지 발송
- 데이터베이스

우리는 이러한 외부 세계에 영향을 받는 코드를 분리해야합니다.

순수함수 사용은 이러한 외부 세계에 영향을 받지 않는 코드를 작성하는 방법입니다.

순수함수란 다음과 같은 특징을 가지는 함수를 말합니다.
- 같은 입력에는 항상 같은 결과
- 외부 세상과 단절된 형태
- 테스트하기 쉬운 코드

### 예시
#### 요구사항
요구사항은 다음과 같습니다.
> 주문은 오전 10시부터 오후 10시 까지만 가능하다.

#### 코드 작성
기존의 코드가 이렇게 작성되어 있다고 가정해보겠습니다.
```java
    public Order createOrder() {
        return new Order(LocalDateTime.now(), beverages);
    }
```

해당 코드는 외부 세계에 영향을 받는 코드입니다.
때문에 테스트에 용이하지 않습니다.
주문시간이 테스트 작동시간에 묶여있기 때문인데요.

이를 순수함수로 변경하고 요구사항에 맞게 변경하겠습니다.
```java
    public Order createOrder(LocalDateTime currentDateTime) {
        LocalTime currentTime = currentDateTime.toLocalTime();
        if (isOperationTime(currentTime)) {
            throw new IllegalArgumentException("주문 시간이 아닙니다. 관리자에게 문의하세요.");
        }

        return new Order(currentDateTime, beverages);
        }
```

이렇게 변경하면 테스트하기 쉬운 코드가 됩니다.
```java

    /**
     * Given: 키오스크가 존재하고<br>
     * Given: 메뉴에 아메리카노가 존재하고<br>
     * Given: 주문 시간이 2023년 1월 17일 10시 0분이라면<br>
     * When: 키오스크에 아메리카노를 추가하고<br>
     * When: 주문을 할 때<br>
     * Then: 주문이 생성된다.<br>
     * Then: 주문에는 아메리카노가 담긴다.<br>
     */
    @Test
    void createOrderWithCurrentTime() {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        LocalDateTime now = LocalDateTime.of(2023, 1, 17, 10, 0);

        //when
        cafeKiosk.add(americano);
        Order order = cafeKiosk.createOrder(now);

        //then
        assertThat(order.getBeverages())
                .hasSize(1)
                .containsOnly(americano);
        assertThat(order.getBeverages())
                .extracting(Beverage::getName)
                .containsOnly("아메리카노");
    }
```