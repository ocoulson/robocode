package deathbot;

import robocode.BattleResults;
import robocode.control.events.BattleCompletedEvent;
import robocode.control.testing.RobotTestBed;

import static org.assertj.core.api.Assertions.*;

import org.junit.BeforeClass;

public class RobotIntegrationTest extends RobotTestBed {
	
	@BeforeClass
	public static void beforeAllRuns() {
		System.setProperty("robocode.home", "\\Users\\olliecoulson\\robocode\\robot\\robocode");
	}

	@Override
	public String getRobotNames() {
		return "sample.SittingDuck, deathbot.DeathBot";
	}

	@Override
	public int getNumRounds() {
		return 10;
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent event) {
		BattleResults result = event.getIndexedResults()[0];
		assertThat(result.getTeamLeaderName()).isEqualTo("deathbot.DeathBot");
	}
	
	

}
