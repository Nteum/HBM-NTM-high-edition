"""
Manim scene introducing an in-game Dolby Atmos pipeline (conceptual).
Render with:
    manim -pqh manim_dolby_atmos_intro.py DolbyAtmosIntro
"""

from manim import (
    DOWN,
    FadeIn,
    FadeOut,
    LEFT,
    RIGHT,
    UP,
    UL,
    UR,
    Arrow,
    AnimationGroup,
    Circle,
    Create,
    Dot,
    LaggedStart,
    Line,
    MarkupText,
    MathTex,
    MoveAlongPath,
    RoundedRectangle,
    Scene,
    Text,
    Transform,
    VGroup,
    Write,
    always_redraw,
    config,
    linear,
)


BACKGROUND = "#0b0d12"
PRIMARY = "#5dc3e6"
SECONDARY = "#9be7c4"
ACCENT = "#ff8c66"
TEXT_COLOR = "#f5f7fb"
CARD_BG = "#121722"
TITLE_FONT = "Helvetica"
BODY_FONT = "Helvetica"
CODE_FONT = "Menlo"

config.background_color = BACKGROUND


class DolbyAtmosIntro(Scene):
    def construct(self):
        title = self._show_title()
        self.wait(1.8)

        stage = self._show_stage_world()
        self.wait(2.2)
        self.play(FadeOut(stage, shift=DOWN * 0.2))

        self.play(FadeOut(title, shift=UP * 0.2))

        vector_math = self._show_vector_math()
        self.wait(3.4)
        self.play(FadeOut(vector_math, shift=DOWN * 0.2))

        spatial = self._show_spatial_walkby()
        self.wait(3.0)
        self.play(FadeOut(spatial, shift=DOWN * 0.2))

        code_math = self._show_code_math_scene()
        self.wait(3.2)
        self.play(FadeOut(code_math, shift=DOWN * 0.2))

        axis2d = self._show_2d_axis_scene()
        self.wait(3.4)
        self.play(FadeOut(axis2d, shift=DOWN * 0.2))

        meta = self._show_metadata()
        self.wait(2.8)
        self.play(FadeOut(meta, shift=DOWN * 0.2))

        render = self._show_renderer()
        self.wait(2.8)

        tips = self._show_tips()
        self.wait(3.2)
        self.play(FadeOut(tips, shift=DOWN * 0.2))

        self.play(FadeOut(render, shift=DOWN * 0.2))
        self.wait(1.0)

    # ------------------------------------------------------------------ #
    def _show_title(self):
        header = Text("游戏内杜比全景声 · 实现概览", color=PRIMARY, font=TITLE_FONT).scale(0.9)
        sub = Text("对象音频 + 元数据 + 渲染器输出", color=TEXT_COLOR, font=BODY_FONT).scale(0.55)
        sub.next_to(header, DOWN, buff=0.25)

        box = RoundedRectangle(
            width=9.6,
            height=2.2,
            corner_radius=0.25,
            stroke_color=PRIMARY,
            fill_color=CARD_BG,
            fill_opacity=0.9,
        )
        content = VGroup(header, sub)
        content.move_to(box.get_center())
        group = VGroup(box, content)

        self.play(Create(box), Write(header))
        self.play(FadeIn(sub, shift=UP * 0.15))
        return group

    def _show_stage_world(self):
        world = RoundedRectangle(
            width=10.0,
            height=5.2,
            corner_radius=0.3,
            stroke_color=PRIMARY,
            fill_color=CARD_BG,
            fill_opacity=0.85,
        )
        label = Text("游戏世界 (声源对象化)", color=PRIMARY, font=BODY_FONT).scale(0.6)
        label.next_to(world.get_top(), DOWN, buff=0.3)

        listener = Dot(color=SECONDARY).scale(1.2)
        listener_label = Text("玩家", color=SECONDARY, font=BODY_FONT).scale(0.45)
        listener_group = VGroup(listener, listener_label).arrange(DOWN, buff=0.12)
        listener_group.move_to(world.get_center())

        s1 = self._sound_node("脚步", (-3.0, 1.2, 0))
        s2 = self._sound_node("爆炸", (2.8, 1.6, 0), color=ACCENT)
        s3 = self._sound_node("机器", (2.4, -1.3, 0))
        s4 = self._sound_node("风声", (-2.6, -1.4, 0), color=PRIMARY)

        arrows = VGroup(
            Arrow(s1[0].get_center(), listener.get_center(), buff=0.2, color=PRIMARY),
            Arrow(s2[0].get_center(), listener.get_center(), buff=0.2, color=ACCENT),
            Arrow(s3[0].get_center(), listener.get_center(), buff=0.2, color=SECONDARY),
            Arrow(s4[0].get_center(), listener.get_center(), buff=0.2, color=PRIMARY),
        )

        pipeline = Text("事件 → 3D对象 → 渲染", color=TEXT_COLOR, font=BODY_FONT).scale(0.5)
        pipeline.next_to(world.get_bottom(), UP, buff=0.3)

        group = VGroup(world, label, listener_group, s1, s2, s3, s4, arrows, pipeline)
        self.play(FadeIn(world, shift=UP * 0.2))
        self.play(Write(label))
        self.play(FadeIn(listener_group, shift=UP * 0.1))
        self.play(LaggedStart(FadeIn(s1), FadeIn(s2), FadeIn(s3), FadeIn(s4), lag_ratio=0.15))
        self.play(Create(arrows), FadeIn(pipeline, shift=UP * 0.1))
        return group

    def _show_metadata(self):
        card = self._card("对象元数据", PRIMARY)
        bullets = self._bullets(
            [
                "位置 (x,y,z) + 速度",
                "尺寸/扩散范围",
                "优先级与可听距离",
                "遮挡/反射系数",
            ]
        )
        self._fit_group(bullets, max_width=4.6, max_height=2.4)
        content = VGroup(card, bullets).arrange(RIGHT, buff=0.9, aligned_edge=UP)
        content.move_to(UP * 0.3)

        budget = self._chip("预算", SECONDARY)
        budget_value = Text("每帧筛选 Top N 对象", color=TEXT_COLOR, font=BODY_FONT).scale(0.45)
        budget_value.next_to(budget, RIGHT, buff=0.25)
        budget_group = VGroup(budget, budget_value)
        budget_group.next_to(bullets, DOWN, buff=0.35).align_to(bullets, LEFT)

        group = VGroup(content, budget_group)
        self.play(FadeIn(card, shift=UP * 0.2))
        self.play(FadeIn(bullets, shift=UP * 0.2))
        self.play(FadeIn(budget_group, shift=UP * 0.2))
        return group

    def _show_renderer(self):
        renderer = self._card("Atmos 渲染器", ACCENT)
        renderer.shift(UP * 0.5)

        bed = self._chip("床声道", PRIMARY)
        bed_desc = Text("环境/音乐", color=TEXT_COLOR, font=BODY_FONT).scale(0.45)
        bed_desc.next_to(bed, DOWN, buff=0.15)
        bed_group = VGroup(bed, bed_desc)
        bed_group.next_to(renderer, LEFT, buff=1.2).shift(DOWN * 0.1)

        objs = self._chip("对象音频", SECONDARY)
        obj_desc = Text("动态声源", color=TEXT_COLOR, font=BODY_FONT).scale(0.45)
        obj_desc.next_to(objs, DOWN, buff=0.15)
        obj_group = VGroup(objs, obj_desc)
        obj_group.next_to(renderer, RIGHT, buff=1.2).shift(DOWN * 0.1)

        arrow_bed = Arrow(bed_group.get_right(), renderer.get_left(), buff=0.2, color=PRIMARY)
        arrow_obj = Arrow(obj_group.get_left(), renderer.get_right(), buff=0.2, color=SECONDARY)

        out_left = self._output_box("耳机输出", "双耳 HRTF", PRIMARY)
        out_right = self._output_box("扬声器输出", "高度声道", SECONDARY)
        out_left.next_to(renderer, DOWN, buff=0.8).shift(LEFT * 2.6)
        out_right.next_to(renderer, DOWN, buff=0.8).shift(RIGHT * 2.6)

        arrow_out_left = Arrow(renderer.get_bottom(), out_left.get_top(), buff=0.2, color=PRIMARY)
        arrow_out_right = Arrow(renderer.get_bottom(), out_right.get_top(), buff=0.2, color=SECONDARY)

        group = VGroup(
            renderer,
            bed_group,
            obj_group,
            arrow_bed,
            arrow_obj,
            out_left,
            out_right,
            arrow_out_left,
            arrow_out_right,
        )
        self.play(FadeIn(renderer, shift=UP * 0.2))
        self.play(FadeIn(bed_group, shift=UP * 0.2), FadeIn(obj_group, shift=UP * 0.2))
        self.play(Create(arrow_bed), Create(arrow_obj))
        self.play(FadeIn(out_left, shift=UP * 0.2), FadeIn(out_right, shift=UP * 0.2))
        self.play(Create(arrow_out_left), Create(arrow_out_right))
        return group

    def _show_tips(self):
        card = self._card("落地要点", PRIMARY)
        bullets = self._bullets(
            [
                "每帧更新对象位置 + 阻挡检测",
                "空间混响作为发送量而非静态滤镜",
                "性能预算：对象合并/裁剪/降级",
                "无 Atmos 设备时回退立体声",
            ]
        )
        self._fit_group(bullets, max_width=4.8, max_height=2.5)
        content = VGroup(card, bullets).arrange(RIGHT, buff=0.9, aligned_edge=UP)
        content.move_to(UP * 0.3)
        group = VGroup(content)
        self.play(FadeIn(card, shift=UP * 0.2))
        self.play(FadeIn(bullets, shift=UP * 0.2))
        return group

    def _show_vector_math(self):
        panel = RoundedRectangle(
            width=10.6,
            height=5.6,
            corner_radius=0.3,
            stroke_color=PRIMARY,
            fill_color=CARD_BG,
            fill_opacity=0.75,
        )

        title = Text("向量数学 · 声源到监听点", color=PRIMARY, font=BODY_FONT).scale(0.6)
        title.next_to(panel.get_top(), DOWN, buff=0.35).align_to(panel, LEFT).shift(RIGHT * 0.5)

        visual = RoundedRectangle(
            width=4.8,
            height=3.4,
            corner_radius=0.25,
            stroke_color=PRIMARY,
            fill_color=BACKGROUND,
            fill_opacity=0.8,
        )
        visual.next_to(panel.get_left(), RIGHT, buff=0.6).shift(DOWN * 0.2)

        orbit = Circle(radius=1.4, color=PRIMARY, stroke_opacity=0.35)
        orbit.move_to(visual.get_center())

        listener = Dot(color=SECONDARY).scale(1.1)
        listener.move_to(visual.get_center())
        listener_label = Text("监听点", color=SECONDARY, font=BODY_FONT).scale(0.4)
        listener_label.next_to(listener, DOWN, buff=0.1)

        source_dot = Dot(color=ACCENT).scale(1.0)
        source_label = Text("声源", color=ACCENT, font=BODY_FONT).scale(0.4)
        source_group = VGroup(source_dot, source_label)
        source_group.move_to(orbit.point_at_angle(0))
        source_label.next_to(source_dot, DOWN, buff=0.1)

        vector_arrow = always_redraw(
            lambda: Arrow(listener.get_center(), source_group.get_center(), buff=0.1, color=PRIMARY)
        )
        vector_label = MathTex(r"\vec{r}", color=PRIMARY).scale(0.55)

        def _update_label(mob):
            mob.next_to(vector_arrow, UP, buff=0.05)

        vector_label.add_updater(_update_label)

        visual_group = VGroup(visual, orbit, listener, listener_label, source_group, vector_arrow, vector_label)

        formulas = VGroup(
            MathTex(r"\vec{r} = \vec{p}_s - \vec{p}_l", color=TEXT_COLOR),
            MathTex(r"d = \|\vec{r}\|", color=TEXT_COLOR),
            MathTex(r"\hat{r} = \vec{r} / d", color=TEXT_COLOR),
            MathTex(r"g = \\frac{1}{1 + k d^2}", color=TEXT_COLOR),
            MathTex(r"L = g \\cdot L_0", color=TEXT_COLOR),
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.24).scale(0.7)

        formula_note = MarkupText("方向决定声像，距离决定衰减", color=TEXT_COLOR, font=BODY_FONT).scale(0.45)
        formula_group = VGroup(formulas, formula_note).arrange(DOWN, aligned_edge=LEFT, buff=0.3)
        formula_group.next_to(visual, RIGHT, buff=0.8).shift(UP * 0.15)

        group = VGroup(panel, title, visual_group, formula_group)

        self.play(FadeIn(panel, shift=UP * 0.2))
        self.play(Write(title))
        self.play(FadeIn(visual_group, shift=UP * 0.2))
        self.play(FadeIn(formula_group, shift=UP * 0.2))
        self.play(MoveAlongPath(source_group, orbit), run_time=4.0, rate_func=linear)
        self.play(MoveAlongPath(source_group, orbit), run_time=3.0, rate_func=linear)

        vector_label.remove_updater(_update_label)
        return group

    def _show_spatial_walkby(self):
        panel = RoundedRectangle(
            width=10.8,
            height=5.8,
            corner_radius=0.3,
            stroke_color=PRIMARY,
            fill_color=CARD_BG,
            fill_opacity=0.75,
        )
        title = Text("三维坐标系 · 走路与擦身而过", color=PRIMARY, font=BODY_FONT).scale(0.58)
        title.next_to(panel.get_top(), DOWN, buff=0.35).align_to(panel, LEFT).shift(RIGHT * 0.5)

        axes = self._make_axes(size=2.6)
        axes.move_to(panel.get_center()).shift(DOWN * 0.2)

        player = Dot(color=SECONDARY).scale(1.1)
        player.move_to(panel.get_center()).shift(DOWN * 0.2)
        player_label = Text("玩家", color=SECONDARY, font=BODY_FONT).scale(0.42)
        player_label.next_to(player, DOWN, buff=0.1)
        player_group = VGroup(player, player_label)

        creature = self._orb_sphere("生物", SECONDARY)
        ambient = self._orb_sphere("环境", PRIMARY)
        machine = self._orb_sphere("机械", ACCENT)

        creature.move_to(player.get_center() + RIGHT * 1.8 + UP * 0.6)
        ambient.move_to(player.get_center() + LEFT * 2.1 + DOWN * 0.5)
        machine.move_to(player.get_center() + RIGHT * 1.3 + DOWN * 1.3)

        creature_path = Circle(radius=1.8).move_to(player.get_center())
        ambient_path = Circle(radius=2.3).move_to(player.get_center()).shift(LEFT * 0.3 + DOWN * 0.2)
        machine_path = Circle(radius=1.4).move_to(player.get_center()).shift(RIGHT * 0.4 + DOWN * 0.4)

        bullet = self._orb_sphere("子弹", ACCENT, scale=0.7)
        arrow = self._orb_sphere("箭矢", PRIMARY, scale=0.7)
        bullet_path = Line(LEFT * 4.4, RIGHT * 4.4).shift(UP * 0.5)
        arrow_path = Line(LEFT * 4.4, RIGHT * 4.4).shift(DOWN * 0.4)
        bullet.move_to(bullet_path.get_start())
        arrow.move_to(arrow_path.get_start())

        group = VGroup(panel, title, axes, player_group, creature, ambient, machine, bullet, arrow)

        self.play(FadeIn(panel, shift=UP * 0.2))
        self.play(Write(title))
        self.play(FadeIn(axes, shift=UP * 0.1), FadeIn(player_group, shift=UP * 0.1))
        self.play(LaggedStart(FadeIn(creature), FadeIn(ambient), FadeIn(machine), lag_ratio=0.15))
        self.play(
            AnimationGroup(
                MoveAlongPath(creature, creature_path),
                MoveAlongPath(ambient, ambient_path),
                MoveAlongPath(machine, machine_path),
                lag_ratio=0.0,
            ),
            run_time=3.4,
            rate_func=linear,
        )
        self.play(
            AnimationGroup(
                MoveAlongPath(bullet, bullet_path),
                MoveAlongPath(arrow, arrow_path),
                lag_ratio=0.1,
            ),
            run_time=1.8,
            rate_func=linear,
        )
        return group

    def _show_code_math_scene(self):
        panel = RoundedRectangle(
            width=10.8,
            height=5.8,
            corner_radius=0.3,
            stroke_color=PRIMARY,
            fill_color=CARD_BG,
            fill_opacity=0.78,
        )

        title = Text("行走/静止 · 代码与公式联动", color=PRIMARY, font=BODY_FONT).scale(0.56)
        title.next_to(panel.get_top(), DOWN, buff=0.35).align_to(panel, LEFT).shift(RIGHT * 0.5)

        code_box = RoundedRectangle(
            width=4.6,
            height=2.4,
            corner_radius=0.2,
            stroke_color=SECONDARY,
            fill_color=BACKGROUND,
            fill_opacity=0.85,
        )
        code_box.next_to(panel.get_corner(UL), DOWN + RIGHT, buff=0.5)

        formula_box = RoundedRectangle(
            width=4.6,
            height=2.4,
            corner_radius=0.2,
            stroke_color=ACCENT,
            fill_color=BACKGROUND,
            fill_opacity=0.85,
        )
        formula_box.next_to(panel.get_corner(UR), DOWN + LEFT, buff=0.5)

        code_title = Text("代码变化", color=SECONDARY, font=BODY_FONT).scale(0.45)
        formula_title = Text("数学公式", color=ACCENT, font=BODY_FONT).scale(0.45)

        code_walk = self._code_block(
            [
                "if (player.moving) {",
                "  src.pos += src.vel * dt;",
                "  d = length(src.pos - pl);",
                "  h = src.pos.y - pl.y;",
                "  gain = falloff(d);",
                "}",
            ]
        )
        code_idle = self._code_block(
            [
                "if (!player.moving) {",
                "  src.pos = src.pos;",
                "  d = cachedDist;",
                "  h = cachedHeight;",
                "  gain = falloff(d);",
                "}",
            ]
        )

        code_stack = VGroup(code_title, code_walk).arrange(DOWN, aligned_edge=LEFT, buff=0.2)
        code_stack.move_to(code_box.get_center()).shift(UP * 0.1)
        self._fit_group(code_stack, max_width=code_box.width - 0.6, max_height=code_box.height - 0.4)

        formula_walk = self._formula_block(
            [
                r"d(t)=\|\vec{p}_s(t)-\vec{p}_l(t)\|",
                r"h(t)=y_s(t)-y_l(t)",
                r"L(t)=L_0 \cdot \frac{1}{1+k d(t)^2}",
            ]
        )
        formula_idle = self._formula_block(
            [
                r"d=\|\vec{p}_s-\vec{p}_l\|",
                r"h=y_s-y_l",
                r"L=L_0 \cdot \frac{1}{1+k d^2}",
            ]
        )

        formula_stack = VGroup(formula_title, formula_walk).arrange(DOWN, aligned_edge=LEFT, buff=0.22)
        formula_stack.move_to(formula_box.get_center()).shift(UP * 0.1)
        self._fit_group(formula_stack, max_width=formula_box.width - 0.6, max_height=formula_box.height - 0.4)

        axes = self._make_axes(size=2.2)
        axes.move_to(panel.get_center()).shift(DOWN * 1.0)
        player = Dot(color=SECONDARY).scale(1.1)
        player.move_to(panel.get_center()).shift(DOWN * 1.0)
        player_label = Text("玩家", color=SECONDARY, font=BODY_FONT).scale(0.4)
        player_label.next_to(player, DOWN, buff=0.1)

        walker = self._orb_sphere("声源A", PRIMARY, scale=0.75)
        walker.move_to(player.get_center() + RIGHT * 1.6 + UP * 0.5)
        walker_path = Circle(radius=1.5).move_to(player.get_center()).shift(UP * 0.1)

        idle_label = Text("玩家行走", color=TEXT_COLOR, font=BODY_FONT).scale(0.5)
        idle_label.next_to(axes, DOWN, buff=0.25)

        group = VGroup(
            panel,
            title,
            code_box,
            formula_box,
            code_stack,
            formula_stack,
            axes,
            player,
            player_label,
            walker,
            idle_label,
        )

        self.play(FadeIn(panel, shift=UP * 0.2))
        self.play(Write(title))
        self.play(FadeIn(code_box, shift=UP * 0.1), FadeIn(formula_box, shift=UP * 0.1))
        self.play(FadeIn(code_stack, shift=UP * 0.1), FadeIn(formula_stack, shift=UP * 0.1))
        self.play(FadeIn(axes, shift=UP * 0.1), FadeIn(player), FadeIn(player_label))
        self.play(FadeIn(walker, shift=UP * 0.1), FadeIn(idle_label, shift=UP * 0.1))
        self.play(MoveAlongPath(walker, walker_path), run_time=2.8, rate_func=linear)

        idle_text = Text("玩家静止", color=TEXT_COLOR, font=BODY_FONT).scale(0.5)
        idle_text.move_to(idle_label.get_center())

        new_code_stack = VGroup(code_title.copy(), code_idle).arrange(
            DOWN, aligned_edge=LEFT, buff=0.2
        )
        new_code_stack.move_to(code_stack.get_center())
        self._fit_group(new_code_stack, max_width=code_box.width - 0.6, max_height=code_box.height - 0.4)

        new_formula_stack = VGroup(formula_title.copy(), formula_idle).arrange(
            DOWN, aligned_edge=LEFT, buff=0.22
        )
        new_formula_stack.move_to(formula_stack.get_center())
        self._fit_group(new_formula_stack, max_width=formula_box.width - 0.6, max_height=formula_box.height - 0.4)

        self.play(
            AnimationGroup(
                FadeOut(idle_label),
                FadeIn(idle_text),
                Transform(code_stack, new_code_stack),
                Transform(formula_stack, new_formula_stack),
                lag_ratio=0.05,
            ),
            run_time=1.8,
        )

        return group

    def _show_2d_axis_scene(self):
        panel = RoundedRectangle(
            width=10.8,
            height=5.8,
            corner_radius=0.3,
            stroke_color=PRIMARY,
            fill_color=CARD_BG,
            fill_opacity=0.78,
        )

        title = Text("二维坐标系 · 纵向位移与声源刷新", color=PRIMARY, font=BODY_FONT).scale(0.56)
        title.next_to(panel.get_top(), DOWN, buff=0.35).align_to(panel, LEFT).shift(RIGHT * 0.5)

        code_box = RoundedRectangle(
            width=4.6,
            height=2.3,
            corner_radius=0.2,
            stroke_color=SECONDARY,
            fill_color=BACKGROUND,
            fill_opacity=0.85,
        )
        code_box.next_to(panel.get_corner(UL), DOWN + RIGHT, buff=0.5)

        formula_box = RoundedRectangle(
            width=4.6,
            height=2.3,
            corner_radius=0.2,
            stroke_color=ACCENT,
            fill_color=BACKGROUND,
            fill_opacity=0.85,
        )
        formula_box.next_to(panel.get_corner(UR), DOWN + LEFT, buff=0.5)

        code_title = Text("代码变换", color=SECONDARY, font=BODY_FONT).scale(0.45)
        formula_title = Text("公式变换", color=ACCENT, font=BODY_FONT).scale(0.45)

        code_stage_1 = self._code_block(
            [
                "pos.y += speed * dt;",
                "d = abs(pos.y - pl.y);",
                "gain = falloff(d);",
            ]
        )
        code_stage_2 = self._code_block(
            [
                "pos.y += speed * dt;",
                "d = hypot(pos.x-pl.x, pos.y-pl.y);",
                "gain = occlusion(d, wall);",
            ]
        )
        code_stage_3 = self._code_block(
            [
                "pos.y += speed * dt;",
                "d = length(pos - pl);",
                "gain = mix(gain, reverb, room);",
            ]
        )

        formula_stage_1 = self._formula_block(
            [
                r"d = |y_s - y_l|",
                r"L = \frac{L_0}{1 + k d^2}",
            ]
        )
        formula_stage_2 = self._formula_block(
            [
                r"d = \sqrt{(x_s-x_l)^2+(y_s-y_l)^2}",
                r"L = L_0 \cdot e^{-\alpha d}",
            ]
        )
        formula_stage_3 = self._formula_block(
            [
                r"d = \|\vec{p}_s-\vec{p}_l\|",
                r"L = (1-\beta)L + \beta R",
            ]
        )

        code_stack = VGroup(code_title, code_stage_1).arrange(DOWN, aligned_edge=LEFT, buff=0.2)
        code_stack.move_to(code_box.get_center()).shift(UP * 0.1)
        self._fit_group(code_stack, max_width=code_box.width - 0.6, max_height=code_box.height - 0.4)

        formula_stack = VGroup(formula_title, formula_stage_1).arrange(DOWN, aligned_edge=LEFT, buff=0.22)
        formula_stack.move_to(formula_box.get_center()).shift(UP * 0.1)
        self._fit_group(formula_stack, max_width=formula_box.width - 0.6, max_height=formula_box.height - 0.4)

        axes = self._make_2d_axes(size=2.4)
        axes.move_to(panel.get_center()).shift(DOWN * 1.0)

        player = self._orb_sphere("玩家", SECONDARY, scale=0.85)
        player.move_to(axes.get_center())
        npc = self._orb_sphere("NPC", PRIMARY, scale=0.8)
        obj = self._orb_sphere("物体", ACCENT, scale=0.8)
        sfx = self._orb_sphere("声源", TEXT_COLOR, scale=0.75)

        npc.move_to(player.get_center() + LEFT * 1.2 + DOWN * 0.5)
        obj.move_to(player.get_center() + RIGHT * 1.1 + DOWN * 0.7)
        sfx.move_to(player.get_center() + RIGHT * 0.2 + DOWN * 1.8)

        npc_path = Line(npc.get_center() + DOWN * 1.4, npc.get_center() + UP * 1.6)
        obj_path = Line(obj.get_center() + DOWN * 1.6, obj.get_center() + UP * 1.4)
        sfx_path = Line(sfx.get_center() + DOWN * 1.8, sfx.get_center() + UP * 1.2)

        group = VGroup(
            panel,
            title,
            code_box,
            formula_box,
            code_stack,
            formula_stack,
            axes,
            player,
            npc,
            obj,
            sfx,
        )

        self.play(FadeIn(panel, shift=UP * 0.2))
        self.play(Write(title))
        self.play(FadeIn(code_box, shift=UP * 0.1), FadeIn(formula_box, shift=UP * 0.1))
        self.play(FadeIn(code_stack, shift=UP * 0.1), FadeIn(formula_stack, shift=UP * 0.1))
        self.play(FadeIn(axes, shift=UP * 0.1), FadeIn(player), FadeIn(npc), FadeIn(obj), FadeIn(sfx))

        self.play(
            AnimationGroup(
                MoveAlongPath(npc, npc_path),
                MoveAlongPath(obj, obj_path),
                MoveAlongPath(sfx, sfx_path),
                lag_ratio=0.05,
            ),
            run_time=2.4,
            rate_func=linear,
        )

        new_code_stack = VGroup(code_title.copy(), code_stage_2).arrange(
            DOWN, aligned_edge=LEFT, buff=0.2
        )
        new_code_stack.move_to(code_stack.get_center())
        self._fit_group(new_code_stack, max_width=code_box.width - 0.6, max_height=code_box.height - 0.4)

        new_formula_stack = VGroup(formula_title.copy(), formula_stage_2).arrange(
            DOWN, aligned_edge=LEFT, buff=0.22
        )
        new_formula_stack.move_to(formula_stack.get_center())
        self._fit_group(new_formula_stack, max_width=formula_box.width - 0.6, max_height=formula_box.height - 0.4)

        self.play(
            AnimationGroup(
                Transform(code_stack, new_code_stack),
                Transform(formula_stack, new_formula_stack),
                lag_ratio=0.05,
            ),
            run_time=1.6,
        )

        self.play(
            AnimationGroup(
                MoveAlongPath(npc, npc_path),
                MoveAlongPath(obj, obj_path),
                MoveAlongPath(sfx, sfx_path),
                lag_ratio=0.05,
            ),
            run_time=2.0,
            rate_func=linear,
        )

        final_code_stack = VGroup(code_title.copy(), code_stage_3).arrange(
            DOWN, aligned_edge=LEFT, buff=0.2
        )
        final_code_stack.move_to(code_stack.get_center())
        self._fit_group(final_code_stack, max_width=code_box.width - 0.6, max_height=code_box.height - 0.4)

        final_formula_stack = VGroup(formula_title.copy(), formula_stage_3).arrange(
            DOWN, aligned_edge=LEFT, buff=0.22
        )
        final_formula_stack.move_to(formula_stack.get_center())
        self._fit_group(final_formula_stack, max_width=formula_box.width - 0.6, max_height=formula_box.height - 0.4)

        self.play(
            AnimationGroup(
                Transform(code_stack, final_code_stack),
                Transform(formula_stack, final_formula_stack),
                lag_ratio=0.05,
            ),
            run_time=1.6,
        )

        self.play(
            AnimationGroup(
                MoveAlongPath(npc, npc_path),
                MoveAlongPath(obj, obj_path),
                MoveAlongPath(sfx, sfx_path),
                lag_ratio=0.05,
            ),
            run_time=2.0,
            rate_func=linear,
        )

        return group

    # ------------------------------------------------------------------ #
    def _card(self, title: str, color: str):
        card = RoundedRectangle(
            width=4.2,
            height=2.1,
            corner_radius=0.2,
            stroke_color=color,
            fill_color=CARD_BG,
            fill_opacity=0.95,
        )
        label = Text(title, color=color, font=BODY_FONT).scale(0.55)
        label.move_to(card.get_center())
        return VGroup(card, label)

    def _bullets(self, lines):
        return VGroup(
            *[
                MarkupText(f"• {line}", color=TEXT_COLOR, font=BODY_FONT).scale(0.48)
                for line in lines
            ]
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.2)

    def _chip(self, text: str, color: str):
        pill = RoundedRectangle(
            width=2.2,
            height=0.7,
            corner_radius=0.25,
            stroke_color=color,
            fill_color=CARD_BG,
            fill_opacity=0.95,
        )
        label = Text(text, color=color, font=BODY_FONT).scale(0.5)
        label.move_to(pill.get_center())
        return VGroup(pill, label)

    def _output_box(self, title: str, subtitle: str, color: str):
        box = RoundedRectangle(
            width=3.2,
            height=1.3,
            corner_radius=0.2,
            stroke_color=color,
            fill_color=CARD_BG,
            fill_opacity=0.95,
        )
        title_text = Text(title, color=color, font=BODY_FONT).scale(0.5)
        subtitle_text = Text(subtitle, color=TEXT_COLOR, font=BODY_FONT).scale(0.42)
        subtitle_text.next_to(title_text, DOWN, buff=0.1)
        group = VGroup(title_text, subtitle_text)
        group.move_to(box.get_center())
        return VGroup(box, group)

    def _sound_node(self, label: str, position, color=PRIMARY):
        dot = Dot(color=color).scale(1.0)
        ring = Circle(radius=0.28, color=color, stroke_opacity=0.7)
        ring.move_to(dot.get_center())
        text = Text(label, color=TEXT_COLOR, font=BODY_FONT).scale(0.45)
        text.next_to(ring, DOWN, buff=0.12)
        group = VGroup(dot, ring, text)
        group.move_to(position)
        return group

    def _orb_sphere(self, label: str, color: str, scale: float = 0.9):
        dot = Dot(color=color).scale(scale)
        ring = Circle(radius=0.28 * scale, color=color, stroke_opacity=0.7)
        ring.move_to(dot.get_center())
        text = Text(label, color=TEXT_COLOR, font=BODY_FONT).scale(0.35)
        text.next_to(ring, DOWN, buff=0.08)
        return VGroup(dot, ring, text)

    def _make_axes(self, size: float = 2.4):
        origin = Dot(color=TEXT_COLOR, fill_opacity=0.0, stroke_opacity=0.0)
        x_axis = Arrow(origin.get_center(), RIGHT * size, buff=0.0, color=PRIMARY)
        y_axis = Arrow(origin.get_center(), UP * size, buff=0.0, color=SECONDARY)
        z_axis = Arrow(origin.get_center(), LEFT * size * 0.6 + UP * size * 0.4, buff=0.0, color=ACCENT)

        x_label = Text("X", color=PRIMARY, font=BODY_FONT).scale(0.35)
        y_label = Text("Y", color=SECONDARY, font=BODY_FONT).scale(0.35)
        z_label = Text("Z", color=ACCENT, font=BODY_FONT).scale(0.35)

        x_label.next_to(x_axis.get_end(), RIGHT, buff=0.05)
        y_label.next_to(y_axis.get_end(), UP, buff=0.05)
        z_label.next_to(z_axis.get_end(), UP, buff=0.05)

        return VGroup(x_axis, y_axis, z_axis, x_label, y_label, z_label)

    def _make_2d_axes(self, size: float = 2.4):
        origin = Dot(color=TEXT_COLOR, fill_opacity=0.0, stroke_opacity=0.0)
        x_axis = Arrow(origin.get_center(), RIGHT * size, buff=0.0, color=PRIMARY)
        y_axis = Arrow(origin.get_center(), UP * size, buff=0.0, color=SECONDARY)
        x_label = Text("X", color=PRIMARY, font=BODY_FONT).scale(0.35)
        y_label = Text("Y", color=SECONDARY, font=BODY_FONT).scale(0.35)
        x_label.next_to(x_axis.get_end(), RIGHT, buff=0.05)
        y_label.next_to(y_axis.get_end(), UP, buff=0.05)
        return VGroup(x_axis, y_axis, x_label, y_label)

    def _code_block(self, lines):
        block = VGroup(
            *[Text(line, color=TEXT_COLOR, font=CODE_FONT).scale(0.33) for line in lines]
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.12)
        return block

    def _formula_block(self, lines):
        block = VGroup(*[MathTex(line, color=TEXT_COLOR) for line in lines])
        block.arrange(DOWN, aligned_edge=LEFT, buff=0.18).scale(0.6)
        return block

    def _fit_group(self, group: VGroup, max_width: float, max_height: float | None = None):
        if group.width == 0 or group.height == 0:
            return group
        max_height = max_height or group.height
        scale = min(max_width / group.width, max_height / group.height, 1.0)
        if scale < 1.0:
            group.scale(scale)
        return group
