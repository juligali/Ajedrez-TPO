package ar.edu.ajedrez;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Properties;
import java.io.OutputStream;
import ar.edu.ajedrez.app.persistence.FileGameRepository;
import ar.edu.ajedrez.core.session.*;
import ar.edu.ajedrez.core.ai.Difficulty;

class ActivityClockTest {
    private final FeatureTest.TestClock clock=new FeatureTest.TestClock();
    private final FeatureTest.MemoryRepository repo=new FeatureTest.MemoryRepository();
    private MatchSession session=FeatureTest.session(repo,clock);
    private void start(){assertTrue(session.newGame(GameMode.TWO_PLAYERS,Difficulty.MEDIUM).isAccepted());}

    @Test void doesNotCountUntilAViewIsActive(){
        start();clock.now+=60000;assertEquals(0,session.getElapsedMillis());
        session.presence("tab",true);clock.now+=4000;assertEquals(4000,session.getElapsedMillis());
    }
    @Test void closeAndReopenExcludeOfflineTime(){
        start();session.presence("tab",true);clock.now+=7000;session.presence("tab",false);
        clock.now+=3600000;session=FeatureTest.session(repo,clock);
        assertEquals(7000,session.getElapsedMillis());assertFalse(session.isClockRunning());
        session.presence("new-tab",true);clock.now+=2000;assertEquals(9000,session.getElapsedMillis());
    }
    @Test void severalViewsCountOnceAndLastClosePauses(){
        start();session.presence("one",true);session.presence("two",true);clock.now+=3000;
        session.presence("one",false);clock.now+=4000;assertEquals(7000,session.getElapsedMillis());
        assertTrue(session.isClockRunning());session.presence("two",false);clock.now+=8000;
        assertEquals(7000,session.getElapsedMillis());assertFalse(session.isClockRunning());
    }
    @Test void lostClientExpiresAndCannotAddAnOfflineGap(){
        start();session.presence("tab",true);clock.now+=60000;
        assertEquals(15000,session.getElapsedMillis());assertFalse(session.isClockRunning());
        session.presence("tab",true);clock.now+=1000;assertEquals(16000,session.getElapsedMillis());
    }
    @Test void checkpointSurvivesAbruptRestartWithoutAClosingEvent(){
        start();session.presence("tab",true);clock.now+=5000;session.presence("tab",true);
        clock.now+=600000;session=FeatureTest.session(repo,clock);
        assertEquals(5000,session.getElapsedMillis());assertFalse(session.isClockRunning());
    }
    @Test void failedMovePreservesClockAndPresence(){
        start();session.presence("tab",true);clock.now+=2000;repo.fail=true;
        assertFalse(session.move(FeatureTest.move("e2","e4")).isAccepted());
        assertTrue(session.getGame().getHistory().isEmpty());clock.now+=1000;
        assertEquals(3000,session.getElapsedMillis());assertTrue(session.isClockRunning());
    }
    @Test void newGameResetsTimeAndKeepsTheVisibleView(){
        start();session.presence("tab",true);clock.now+=4000;start();clock.now+=1000;
        assertEquals(1000,session.getElapsedMillis());assertTrue(session.isClockRunning());
    }
    @Test void fileFormatPreservesAccumulatedTime(@TempDir Path dir){
        FileGameRepository disk=new FileGameRepository(dir.resolve("match.properties"));
        session=FeatureTest.session(disk,clock);start();session.presence("tab",true);
        clock.now+=4500;session.presence("tab",false);clock.now+=60000;
        session=FeatureTest.session(disk,clock);assertEquals(4500,session.getElapsedMillis());
    }
    @Test void migratesLegacySaveOnlyUntilItsLastWrite(@TempDir Path dir) throws Exception {
        Path file=dir.resolve("legacy.properties");Properties p=new Properties();
        p.setProperty("version","1");p.setProperty("count","0");p.setProperty("cursor","0");
        p.setProperty("mode","TWO_PLAYERS");p.setProperty("difficulty","MEDIUM");
        p.setProperty("startedAt","100000");p.setProperty("endedAt","0");p.setProperty("configured","true");
        try(OutputStream out=Files.newOutputStream(file)){p.store(out,"legacy");}
        Files.setLastModifiedTime(file,FileTime.fromMillis(107000));clock.now=1000000;
        FileGameRepository disk=new FileGameRepository(file);session=FeatureTest.session(disk,clock);
        assertEquals(7000,session.getElapsedMillis());session.save();
        assertEquals(7000,FeatureTest.session(disk,clock).getElapsedMillis());
    }
}
