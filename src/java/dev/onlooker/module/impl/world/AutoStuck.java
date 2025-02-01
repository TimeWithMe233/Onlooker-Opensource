package dev.onlooker.module.impl.world;

import dev.onlooker.Client;
import dev.onlooker.event.impl.player.MotionEvent;
import dev.onlooker.module.Category;
import dev.onlooker.module.Module;
import dev.onlooker.module.settings.impl.NumberSetting;
import dev.onlooker.utils.misc.MathUtil;
import dev.onlooker.utils.player.ChatUtil;
import dev.onlooker.utils.player.FallDistanceComponent;
import dev.onlooker.utils.player.PlayerUtil;
import dev.onlooker.utils.player.ProjectileUtil;
import dev.onlooker.utils.time.StopWatch;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;

import javax.vecmath.Vector2f;

public class AutoStuck extends Module {
    private final NumberSetting falldistance = new NumberSetting("Fall Distance",2.0f, 10.0f,2.0f,1.0f);
    private CalculateThread calculateThread;
    Stuck stuck = (Stuck) Client.INSTANCE.getModuleCollection().get(Stuck.class);
    private boolean attempted;
    private boolean calculating;
    private int bestPearlSlot;
    public AutoStuck() {
        super("AutoStuck", Category.WORLD, "falling into the void and automatically getting stuck");
        addSettings(falldistance);
    }
    @Override
    public void onMotionEvent(MotionEvent event) {
        if (AutoStuck.mc.thePlayer.onGround) {
            this.attempted = false;
            this.calculating = false;
        }
        if (event.isPost() && this.calculating && (this.calculateThread == null || this.calculateThread.completed)) {
            this.calculating = false;
            stuck.throwPearl(this.calculateThread.solution);
        }
        final boolean overVoid = !AutoStuck.mc.thePlayer.onGround && !PlayerUtil.isBlockUnder(30.0, true);
        if (!this.attempted && !AutoStuck.mc.thePlayer.onGround && overVoid && FallDistanceComponent.distance > falldistance.getValue()) {
            FallDistanceComponent.distance = 0.0f;
            this.attempted = true;
            for (int slot = 5; slot < 45; ++slot) {
                final ItemStack stack = AutoStuck.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                if (stack != null && stack.getItem() instanceof ItemEnderPearl && slot >= 36) {
                    this.bestPearlSlot = slot;
                }
            }
            if (this.bestPearlSlot == 0) {
                return;
            }
            ChatUtil.print(this.bestPearlSlot);
            if (!(AutoStuck.mc.thePlayer.inventoryContainer.getSlot(this.bestPearlSlot).getStack().getItem() instanceof ItemEnderPearl)) {
                return;
            }
            this.calculating = true;
            (this.calculateThread = new CalculateThread(AutoStuck.mc.thePlayer.posX, AutoStuck.mc.thePlayer.posY, AutoStuck.mc.thePlayer.posZ, 0.0, 0.0)).start();
            Client.INSTANCE.getModuleCollection().get(Stuck.class).toggleSilent(true);
        }
    }
    private static class CalculateThread extends Thread
    {
        private int iteration;
        private boolean completed;
        private double temperature;
        private double energy;
        private double solutionE;
        private Vector2f solution;
        public boolean stop;
        private final ProjectileUtil.EnderPearlPredictor predictor;

        private CalculateThread(final double predictX, final double predictY, final double predictZ, final double minMotionY, final double maxMotionY) {
            this.predictor = new ProjectileUtil.EnderPearlPredictor(predictX, predictY, predictZ, minMotionY, maxMotionY);
            this.iteration = 0;
            this.temperature = 10.0;
            this.energy = 0.0;
            this.stop = false;
            this.completed = false;
        }

        @Override
        public void run() {
            final StopWatch timer = new StopWatch();
            timer.reset();
            this.solution = new Vector2f((float)MathUtil.getRandomInRange(-180, 180), (float)MathUtil.getRandomInRange(-90, 90));
            this.energy = this.predictor.assessRotation(this.solution);
            this.solutionE = this.energy;
                this.temperature *= 0.997;
                ++this.iteration;
            }
        }
    }

