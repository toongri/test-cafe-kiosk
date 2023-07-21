package sample.cafekiosk;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class CafeKioskTest {

    @Test
    void add_manual_test() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());

        System.out.println(">>> 담긴 음료 수 : " + cafeKiosk.getBeverages().size());
        System.out.println(">>> 담긴 음료 : " + cafeKiosk.getBeverages().get(0).getName());
    }

    @Test
    void add() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        cafeKiosk.add(new Americano());

        assertThat(cafeKiosk.getBeverages()).hasSize(1);
        assertThat(cafeKiosk.getBeverages().get(0).getName()).isEqualTo("아메리카노");
    }

    /**
     * Given: 키오스크가 존재하고<br>
     * Given: 메뉴에 아메리카노가 존재하다면<br>
     * When: 음료 2잔을 키오스크에 추가할 때<br>
     * Then: 음료가 2잔 추가된다.<br>
     */
    @Test
    void addSeveralBeverages() {
        //given
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        //when
        cafeKiosk.add(americano, 2);

        //then
        assertThat(cafeKiosk.getBeverages()).hasSize(2).containsOnly(americano);
    }

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

    @Test
    void remove() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();

        cafeKiosk.add(americano);
        assertThat(cafeKiosk.getBeverages()).hasSize(1);

        cafeKiosk.remove(americano);
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

    @Test
    void clear() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        Latte latte = new Latte();

        cafeKiosk.add(americano);
        cafeKiosk.add(latte);
        assertThat(cafeKiosk.getBeverages()).hasSize(2);

        cafeKiosk.clear();
        assertThat(cafeKiosk.getBeverages()).isEmpty();
    }

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

    @Test
    void createOrderOutsideOpenTime() {
        CafeKiosk cafeKiosk = new CafeKiosk();
        Americano americano = new Americano();
        cafeKiosk.add(americano);

        assertThatThrownBy(() -> cafeKiosk.createOrder(LocalDateTime.of(2023, 1, 17, 9, 59)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문 시간이 아닙니다. 관리자에게 문의하세요.");
    }

}
