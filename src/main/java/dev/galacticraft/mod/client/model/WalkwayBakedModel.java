/*
 * Copyright (c) 2019-2025 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.client.model;

import com.mojang.math.Axis;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.content.block.entity.WalkwayBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class WalkwayBakedModel implements BakedModel {

    private static WalkwayBakedModel instance = null;
    private BakedModel walkway;
    private final TextureAtlasSprite sprite;
    public static final ResourceLocation WALKWAY_PLATFORM = Constant.id("block/walkway");
    private static final ResourceLocation WALKWAY_TEXTURE = Constant.id("block/walkway");

    public WalkwayBakedModel(ModelBaker baker, Function<Material, TextureAtlasSprite> textureGetter, ModelState state) {
        this.sprite = textureGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, WALKWAY_TEXTURE));
        this.walkway = baker.getModel(WALKWAY_PLATFORM).bake(baker, textureGetter, state);
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<RandomSource> randomSupplier, RenderContext context) {
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.sprite;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    public static WalkwayBakedModel getInstance(ModelBaker loader, Function<Material, TextureAtlasSprite> spriteFunction, ModelState rotationContainer) {
        if (instance == null) {
            return instance = new WalkwayBakedModel(loader, spriteFunction, rotationContainer);
        }
        return instance;
    }

    public static void invalidate() {
        instance = null;
    }

    public BakedModel getWalkway() {
        return walkway;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter getter, BlockState blockState, BlockPos blockpos, Supplier<RandomSource> randomSupplier, RenderContext context) {
        var emitter = context.getEmitter();
        int X = blockpos.getX();
        int Y = blockpos.getY();
        int Z = blockpos.getZ();
        boolean up =getter.getBlockEntity(new BlockPos(X, Y + 1, Z)) instanceof WalkwayBlockEntity walkway2 && walkway2.getDirection() != Direction.DOWN;
        boolean down =getter.getBlockEntity(new BlockPos(X, Y - 1, Z)) instanceof WalkwayBlockEntity walkway2 && walkway2.getDirection() != Direction.UP;
        boolean north =getter.getBlockEntity(new BlockPos(X, Y, Z - 1)) instanceof WalkwayBlockEntity walkway2 && walkway2.getDirection() != Direction.SOUTH;
        boolean south =getter.getBlockEntity(new BlockPos(X, Y, Z + 1)) instanceof WalkwayBlockEntity walkway2 && walkway2.getDirection() != Direction.NORTH;
        boolean east =getter.getBlockEntity(new BlockPos(X + 1, Y, Z)) instanceof WalkwayBlockEntity walkway2 && walkway2.getDirection() != Direction.WEST;
        boolean west =getter.getBlockEntity(new BlockPos(X - 1, Y, Z)) instanceof WalkwayBlockEntity walkway2 && walkway2.getDirection() != Direction.EAST;
        RenderContext.QuadTransform walkwayTransform = quad -> {
            quad.spriteBake(this.sprite, MutableQuadView.BAKE_LOCK_UV);
            return true;
        };
        context.pushTransform(walkwayTransform);
        if (getter.getBlockEntity(blockpos) instanceof WalkwayBlockEntity walkway) {
            if (walkway.getDirection() != Direction.UP && up) {
                emitter.square(Direction.EAST, 1.0f - 6.0f/16.0f, 1.0f, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.WEST, 1.0f - 6.0f/16.0f, 1.0f, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.SOUTH, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 1.0f - 6.0f/16.0f, 1.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.NORTH, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 1.0f - 6.0f/16.0f, 1.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
            }
            if (walkway.getDirection() != Direction.DOWN && down) {
                emitter.square(Direction.EAST, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 6.0f/16.0f, 0.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.WEST, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 6.0f/16.0f, 0.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.SOUTH, 6.0f/16.0f, 0.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.NORTH, 6.0f/16.0f, 0.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
            }
            if (walkway.getDirection() != Direction.EAST && east) {
                emitter.square(Direction.SOUTH, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 1.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.NORTH, 0.0f, 6.0f/16.0f, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.UP, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 1.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.DOWN, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 1.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
            }
            if (walkway.getDirection() != Direction.WEST && west) {
                emitter.square(Direction.SOUTH, 0.0f, 6.0f/16.0f, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.NORTH, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 1.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.UP, 0.0f, 6.0f/16.0f, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.DOWN, 0.0f, 6.0f/16.0f, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
            }
            if (walkway.getDirection() != Direction.NORTH && north) {
                emitter.square(Direction.EAST, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 1.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.WEST, 0.0f, 6.0f/16.0f, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.UP, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 1.0f - 6.0f/16.0f, 1.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.DOWN, 6.0f/16.0f, 0.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
            }
            if (walkway.getDirection() != Direction.SOUTH && south) {
                emitter.square(Direction.EAST, 0.0f, 6.0f/16.0f, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.WEST, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 1.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(0, 6, 4).uv(1, 9, 4).uv(2, 9, 11).uv(3, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.UP, 6.0f/16.0f, 0.0f, 1.0f - 6.0f/16.0f, 6.0f/16.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
                emitter.square(Direction.DOWN, 6.0f/16.0f, 1.0f - 6.0f/16.0f, 1.0f - 6.0f/16.0f, 1.0f, 6.0f/16.0f).color(-1, -1, -1, -1).uv(1, 6, 4).uv(2, 9, 4).uv(3, 9, 11).uv(0, 6, 11).spriteBake(this.sprite, MutableQuadView.BAKE_NORMALIZED & MutableQuadView.BAKE_LOCK_UV).emit();
            }
            context.popTransform();
                var x = 0;
                var y = 0;
                switch (walkway.getDirection()) {
                    case DOWN -> x = 180;
                    case NORTH -> x = 270;
                    case SOUTH -> x = 90;
                    case EAST -> {
                        x = 90;
                        y = 90;
                    }
                    case WEST -> {
                        x = 90;
                        y = 270;
                    }
                }

                Transform.INSTANCE.setQuaternions(Axis.XP.rotationDegrees(x), Axis.YP.rotationDegrees(y));
                context.pushTransform(Transform.INSTANCE);
                this.walkway.emitBlockQuads(getter, blockState, blockpos, randomSupplier, context);
                context.popTransform();
        }
    }

    public enum Transform implements RenderContext.QuadTransform {
        INSTANCE;

        private Quaternionf quaternionX;
        private Quaternionf quaternionY;
        private final Vector3f vec = new Vector3f();

        public void setQuaternions(Quaternionf quaternionX, Quaternionf quaternionY) {
            this.quaternionX = quaternionX;
            this.quaternionY = quaternionY;
        }

        @Override
        public boolean transform(MutableQuadView quad) {
            for (var i = 0; i < 4; i++) {
                quad.copyPos(i, this.vec);
                this.vec.set(this.vec.x() - 0.5f, this.vec.y() - 0.5f, this.vec.z() - 0.5f);
                this.quaternionX.transform(this.vec);
                this.quaternionY.transform(this.vec);
                this.vec.set(this.vec.x() + 0.5f, this.vec.y() + 0.5f, this.vec.z() + 0.5f);
                quad.pos(i, this.vec);
            }
            return true;
        }
    }
}
