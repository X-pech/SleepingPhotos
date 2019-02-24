package study.itmo.xpech.mdft;

import android.support.test.rule.ActivityTestRule;
import android.support.test.espresso.base.ActiveRootLister;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class RecyclerTest {

    @Rule
    public ActivityTestRule<ItemListActivity> activityTestRule = new ActivityTestRule<>(ItemListActivity.class);
    private ItemListActivity listActivity;

    @Before
    public void setUp() {
        listActivity = activityTestRule.getActivity();
    }

    @Test
    public void testRView() {
        View recycler = listActivity.findViewById(R.id.item_list);
        assertNotNull(recycler);
    }
}
