package fire.ui.dialogs;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.input.KeyCode;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.scene.Group;
import arc.scene.actions.RelativeTemporalAction;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.OrderedSet;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Nullable;
import arc.util.Scaling;
import fire.content.FRBlocks;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.layout.BranchTreeLayout;
import mindustry.ui.layout.TreeLayout;
import mindustry.world.Block;

import java.util.Arrays;

import static mindustry.Vars.*;

/**
 * To show more details about contents.
 * @see mindustry.ui.dialogs.ResearchDialog
 */
public class InfoDialog extends BaseDialog{

    private final View view;
    private Rect bounds = new Rect();
    private final OrderedSet<InfoTreeNode> nodes = new OrderedSet<>();
    private InfoTreeNode root = new InfoTreeNode(InfoTree.roots.first(), null);
    private InfoNode lastNode = root.node;

    private static final float nodeSize = Scl.scl(70.0f);
    private static final float nodeSpacing = 40.0f;
    public static final InfoDialog dialog = new InfoDialog();

    public InfoDialog(){
        super("");

        titleTable.remove();
        titleTable.clear();
        titleTable.top();
        titleTable.button(b -> {
            b.imageDraw(() -> new TextureRegionDrawable(root.node.content.uiIcon)).padRight(8).size(iconMed);
            b.add().growX();
            b.label(() -> root.node.content.localizedName).color(Pal.accent);
            b.add().growX();
            b.add().size(iconMed);

        }, () -> new BaseDialog("@techtree.select"){{

            cont.pane(t -> t.table(Tex.button, in -> {
                in.defaults().width(300f).height(60f);

                var roots = InfoTree.roots;
                for(var node : roots){
                    if(locked(node)) continue;

                    in.button(node.content.localizedName, new TextureRegionDrawable(root.node.content.uiIcon), Styles.flatTogglet, iconMed, () -> {
                        if(node == lastNode) return;

                        rebuildTree(node);
                        hide();
                    }).marginLeft(12f).checked(node == lastNode).row();
                }
            }));

            addCloseButton();
        }}.show()).minWidth(300f);

        margin(0f).marginBottom(8);
        cont.stack(titleTable, view = new View()).grow();

        titleTable.toFront();
        shouldPause = true;

        shown(() -> {
            checkNodes(root);
            treeLayout();

            view.hoverNode = null;
            view.infoTable.remove();
            view.infoTable.clear();
        });

        addCloseButton();

        addListener(new InputListener(){

            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY){
                view.setScale(Mathf.clamp(view.scaleX - amountY / 10f * view.scaleX, 0.25f, 1f));
                view.setOrigin(Align.center);
                view.setTransform(true);
                return true;
            }

            @Override
            public boolean mouseMoved(InputEvent event, float x, float y){
                view.requestScroll();
                return super.mouseMoved(event, x, y);
            }
        });

        touchable = Touchable.enabled;

        addCaptureListener(new ElementGestureListener(){

            @Override
            public void zoom(InputEvent event, float initialDistance, float distance){
                if(view.lastZoom < 0)
                    view.lastZoom = view.scaleX;

                view.setScale(Mathf.clamp(distance / initialDistance * view.lastZoom, 0.25f, 1f));
                view.setOrigin(Align.center);
                view.setTransform(true);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button){
                view.lastZoom = view.scaleX;
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY){
                view.panX += deltaX / view.scaleX;
                view.panY += deltaY / view.scaleY;
                view.moved = true;
                view.clamp();
            }
        });
    }

    private void switchTree(InfoNode node){
        if(lastNode == node || node == null) return;

        nodes.clear();
        root = new InfoTreeNode(node, null);
        lastNode = node;
        view.rebuildAll();
    }

    private void rebuildTree(InfoNode node){
        switchTree(node);

        view.panX = 0f;
        view.panY = -200f;
        view.setScale(1f);
        view.hoverNode = null;
        view.infoTable.remove();
        view.infoTable.clear();

        checkNodes(root);
        treeLayout();
    }

    private void checkNodes(InfoTreeNode node){
        boolean locked = locked(node.node);
        if(!locked && (node.parent == null || node.parent.visible)) node.visible = true;

        node.selectable = !locked(node.node);

        var children = node.children;
        for(var n : children){
            n.visible = !locked && n.parent.visible;
            checkNodes(n);
        }
    }

    private boolean locked(InfoNode node){
        return node.content.locked();
    }

    private void treeLayout(){
        var node = new LayoutNode(root, null);
        var children = node.children;
        var leftHalf = Arrays.copyOfRange(node.children, 0, Mathf.ceil(node.children.length / 2f));
        var rightHalf = Arrays.copyOfRange(node.children, Mathf.ceil(node.children.length / 2f), node.children.length);

        node.children = leftHalf;
        new BranchTreeLayout(){{
            gapBetweenLevels = gapBetweenNodes = nodeSpacing;
            rootLocation = TreeLocation.top;
        }}.layout(node);

        float lastY = node.y;

        if(rightHalf.length > 0){

            node.children = rightHalf;
            new BranchTreeLayout(){{
                gapBetweenLevels = gapBetweenNodes = nodeSpacing;
                rootLocation = TreeLocation.bottom;
            }}.layout(node);

            shift(leftHalf, node.y - lastY);
        }

        node.children = children;

        float minX = 0f, minY = 0f, maxX = 0f, maxY = 0f;
        copyInfo(node);

        var nodes = this.nodes;
        for(var n : nodes){
            if(!n.visible) continue;

            minX = Math.min(n.x - n.width / 2f, minX);
            maxX = Math.max(n.x + n.width / 2f, maxX);
            minY = Math.min(n.y - n.height / 2f, minY);
            maxY = Math.max(n.y + n.height / 2f, maxY);
        }
        bounds = new Rect(minX, minY, maxX - minX, maxY - minY);
        bounds.y += nodeSize * 1.5f;
    }

    private void shift(LayoutNode[] children, float amount){
        for(var node : children){
            node.y += amount;

            if(node.children != null && node.children.length > 0)
                shift(node.children, amount);
        }
    }

    private void copyInfo(LayoutNode node){
        node.node.x = node.x;
        node.node.y = node.y;

        if(node.children != null){
            var children = node.children;
            for(var child : children)
                copyInfo(child);
        }
    }

    private class View extends Group{

        private float panX = 0f, panY = -200f, lastZoom = -1f;
        private boolean moved = false;
        private ImageButton hoverNode;
        private final Table infoTable = new Table();

        {
            rebuildAll();
        }

        private void rebuildAll(){
            clear();
            hoverNode = null;
            infoTable.clear();
            infoTable.touchable = Touchable.enabled;

            var nodes = InfoDialog.this.nodes;
            for(var node : nodes){
                var button = new ImageButton(node.node.content.uiIcon, Styles.nodei);

                button.resizeImage(32f);
                button.getImage().setScaling(Scaling.fit);
                button.visible(() -> node.visible);
                button.clicked(() -> {
                    if(moved) return;

                    if(mobile){
                        hoverNode = button;
                        rebuild();

                        float right = infoTable.getRight();
                        if(right > Core.graphics.getWidth()){
                            addAction(new RelativeTemporalAction(){
                                @Override
                                protected void updateRelative(float percentDelta){
                                    panX -= (right - Core.graphics.getWidth()) * percentDelta;
                                    setDuration(0.1f);
                                    setInterpolation(Interp.fade);
                                }
                            });
                        }
                    }
                });

                button.hovered(() -> {
                    if(!mobile && hoverNode != button && node.visible){
                        hoverNode = button;
                        rebuild();
                    }
                });

                button.exited(() -> {
                    if(!mobile && hoverNode == button && !infoTable.hasMouse() && !hoverNode.hasMouse()){
                        hoverNode = null;
                        rebuild();
                    }
                });

                button.touchable(() -> !node.visible ? Touchable.disabled : Touchable.enabled);
                button.userObject = node.node;
                button.setSize(nodeSize);

                button.update(() -> {
                    float offset = (Core.graphics.getHeight() % 2f) / 2f;
                    button.setPosition(node.x + panX + width / 2f, node.y + panY + height / 2f + offset, Align.center);
                    button.getStyle().up = !locked(node.node) ? Tex.buttonOver : locked(node.node) || !locked(node.node) ? Tex.buttonRed : Tex.button;

                    ((TextureRegionDrawable)button.getStyle().imageUp).setRegion(node.selectable ? node.node.content.uiIcon : Icon.lock.getRegion());
                    button.getImage().setColor(!locked(node.node) ? Color.white : node.selectable ? Color.gray : Pal.gray);
                    button.getImage().layout();
                });
                addChild(button);
            }

            if(mobile)
                tapped(() -> {
                    if(Core.scene.hit(Core.input.mouseX(), Core.input.mouseY(), true) == this){
                        hoverNode = null;
                        rebuild();
                    }
                });

            setOrigin(Align.center);
            setTransform(true);
            released(() -> moved = false);
        }

        private void clamp(){
            float pad = nodeSize;
            float ox = width / 2.0f, oy = height / 2.0f;
            float rw = bounds.width, rh = bounds.height;

            float rx = bounds.x + panX + ox, ry = panY + oy + bounds.y;

            rx = Mathf.clamp(rx, -rw + pad, Core.graphics.getWidth() - pad);
            ry = Mathf.clamp(ry, -rh + pad, Core.graphics.getHeight() - pad);
            panX = rx - bounds.x - ox;
            panY = ry - bounds.y - oy;
        }

        private String getKey(InfoNode node){
            return node.content.getContentType() + "." + node.content.name + ".info";
        }

        private boolean hasInfo(InfoNode node){
            return !Core.bundle.get(getKey(node)).startsWith("?");
        }

        private void rebuild(){
            var button = hoverNode;

            infoTable.remove();
            infoTable.clear();
            infoTable.update(null);
            if(button == null) return;

            var node = (InfoNode)button.userObject;
            infoTable.exited(() -> {
                if(hoverNode == button && !infoTable.hasMouse() && !hoverNode.hasMouse()){
                    hoverNode = null;
                    rebuild();
                }
            });

            infoTable.update(() -> infoTable.setPosition(button.x + button.getWidth(), button.y + button.getHeight(), Align.topLeft));
            infoTable.left();
            infoTable.background(Tex.button).margin(8f);
            infoTable.table(b -> {
                b.margin(0).left().defaults().left();
                b.add().grow();

                if(!locked(node)) b.button(Icon.info, Styles.flati, () -> {
                    ui.content.show(node.content);
                    hide();
                }).growY().width(50f);

                var t = b.table(desc -> {
                    desc.left().defaults().left();
                    desc.add(locked(node) ? "@fire.lockedcontent" : node.content.localizedName);
                    desc.row();

                    if(locked(node))
                        desc.table(Table::left);
                    else
                        desc.add("@completed");

                }).pad(9f);

                if(hasInfo(node)) t.minWidth(480f);
            });

            infoTable.row();

            if(!locked(node) && hasInfo(node))
                infoTable.table(t -> t.margin(3f).left().labelWrap(
                    FRBlocks.compositeMap.containsKey(node.content.id)
                    ? Core.bundle.get(getKey(node)) + Core.bundle.format("composite.info", ((Block)content.getByID(ContentType.block, FRBlocks.compositeMap.get(node.content.id))).localizedName)
                    : Core.bundle.get(getKey(node))
                ).color(Color.lightGray).growX()).fillX();

            addChild(infoTable);

            infoTable.pack();
            infoTable.act(Core.graphics.getDeltaTime());
        }

        @Override
        public void drawChildren(){
            clamp();
            float offsetX = panX + width * 0.5f, offsetY = panY + height * 0.5f;
            Draw.sort(true);

            var nodes = InfoDialog.this.nodes;
            for(var node : nodes){
                if(!node.visible) continue;

                var children = node.children;
                for(var child : children){
                    if(!child.visible) continue;

                    boolean lock = locked(node.node) || locked(child.node);
                    Draw.z(lock ? 1f : 2f);

                    Lines.stroke(Scl.scl(4f), lock ? Pal.gray : Pal.accent);
                    Draw.alpha(parentAlpha);

                    if(Mathf.equal(Math.abs(node.y - child.y), Math.abs(node.x - child.x), 1f) && Mathf.dstm(node.x, node.y, child.x, child.y) <= node.width * 3f){
                        Lines.line(node.x + offsetX, node.y + offsetY, child.x + offsetX, child.y + offsetY);
                    }else{
                        Lines.line(node.x + offsetX, node.y + offsetY, child.x + offsetX, node.y + offsetY);
                        Lines.line(child.x + offsetX, node.y + offsetY, child.x + offsetX, child.y + offsetY);
                    }
                }
            }

            Draw.sort(false);
            Draw.reset();
            super.drawChildren();
        }
    }

    private class LayoutNode extends TreeLayout.TreeNode<LayoutNode>{

        private final InfoTreeNode node;

        private LayoutNode(InfoTreeNode node, LayoutNode parent){
            this.node = node;
            this.parent = parent;
            this.width = this.height = nodeSize;

            if(node.children != null)
                children = Seq.with(node.children).map(t -> new LayoutNode(t, this)).toArray(LayoutNode.class);
        }
    }

    public class InfoTreeNode extends TreeLayout.TreeNode<InfoTreeNode>{

        private final InfoNode node;
        private boolean visible = true, selectable = true;

        private InfoTreeNode(InfoNode node, InfoTreeNode parent){
            this.node = node;
            this.parent = parent;
            this.width = this.height = nodeSize;

            nodes.add(this);
            children = new InfoTreeNode[node.children.size];

            for(byte i = 0, len = (byte)children.length; i < len; i++)
                children[i] = new InfoTreeNode(node.children.get(i), this);
        }
    }


    public static class InfoTree{
        public static final Seq<InfoNode> all = new Seq<>();
        public static final Seq<InfoNode> roots = new Seq<>();
        private static InfoNode context;
    }

    public static class InfoNode{

        private final short depth;
        private final UnlockableContent content;
        private final Seq<InfoNode> children = new Seq<>();

        private InfoNode(@Nullable InfoNode parent, UnlockableContent content){
            if(parent != null) parent.children.add(this);

            this.content = content;
            this.depth = (short)(parent == null ? 0 : parent.depth + 1);

            InfoTree.all.add(this);
        }

        public static InfoNode dnode(UnlockableContent content){
            return dnode(content, () -> {

            });
        }

        public static InfoNode dnode(UnlockableContent content, Runnable children){
            return dnode(content, false, children);
        }

        public static InfoNode dnode(UnlockableContent content, boolean isRoot, Runnable children){
            if(isRoot) InfoTree.roots.add(dnode(content, children));

            var node = new InfoNode(InfoTree.context, content);
            var prev = InfoTree.context;

            InfoTree.context = node;
            children.run();
            InfoTree.context = prev;

            return node;
        }
    }
}
