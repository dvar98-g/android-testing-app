package com.globant.mobile.tests.swipe;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.globant.mobile.screens.common.BottomTabBarComponent;
import com.globant.mobile.screens.common.ScreenNames;
import com.globant.mobile.screens.swipe.SwipeScreen;
import com.globant.mobile.utils.BaseTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Escenario: Swipe cards on the Swipe section.
 * <p>
 * Precondicion comun: estar en la seccion Swipe, verificada en @BeforeMethod.
 * <p>
 * El escenario esta partido en tests independientes (en vez de un unico flujo
 * secuencial) para poder identificar con precision en que paso falla: cada uno
 * arma su propia precondicion desde cero y verifica un solo comportamiento.
 */
public class SwipeTest extends BaseTest {

    private SwipeScreen swipeScreen;

    @BeforeMethod
    public void navigateToSwipeSection() {
        BottomTabBarComponent tabBar = new BottomTabBarComponent(driver);
        tabBar.tapTab(ScreenNames.SWIPE);

        swipeScreen = new SwipeScreen(driver);
        assertThat("Swipe screen displayed as precondition", swipeScreen.isScreenDisplayed(), is(true));
    }

    @Test
    public void carousel_startsOnFirstCard() {
        assertThat("Carousel displayed", swipeScreen.isCarouselDisplayed(), is(true));
        assertThat("First card displayed",
                swipeScreen.isCardDisplayed(SwipeScreen.CARD_FULLY_OPEN_SOURCE), is(true));
    }

    @Test
    public void swipeOnce_showsSecondCard_andHidesFirst() {
        swipeScreen.swipeToNextCard();

        assertThat("Second card displayed after swipe",
                swipeScreen.isCardDisplayed(SwipeScreen.CARD_GREAT_COMMUNITY), is(true));
        assertThat("First card hidden after swipe",
                swipeScreen.isCardHidden(SwipeScreen.CARD_FULLY_OPEN_SOURCE), is(true));
    }

    @Test
    public void swipeThroughAllCards_showsEachCardInOrder() {
        for (int index = 1; index < SwipeScreen.CARDS_IN_ORDER.size(); index++) {
            String previousCard = SwipeScreen.CARDS_IN_ORDER.get(index - 1);
            String currentCard = SwipeScreen.CARDS_IN_ORDER.get(index);

            swipeScreen.swipeToNextCard();

            assertThat("Card '" + currentCard + "' displayed after swipe",
                    swipeScreen.isCardDisplayed(currentCard), is(true));
            assertThat("Previous card '" + previousCard + "' hidden after swipe",
                    swipeScreen.isCardHidden(previousCard), is(true));
        }
    }

    @Test
    public void swipeToLastCard_leavesOnlyThatCardRendered() {
        swipeScreen.swipeToLastCard();

        assertThat("Last card displayed",
                swipeScreen.isCardDisplayed(SwipeScreen.CARD_COMPATIBLE), is(true));
        assertThat("Last card is the only one rendered in the carousel",
                swipeScreen.getRenderedCarouselItemCount(), is(equalTo(1)));
    }

    @Test
    public void swipeVertically_revealsHiddenText() {
        swipeScreen.swipeVerticallyUntilHiddenTextIsFound();

        assertThat("Hidden text found after vertical swipe",
                swipeScreen.getHiddenText(), is(equalTo(SwipeScreen.HIDDEN_TEXT)));
    }
}