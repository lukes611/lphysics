import java.awt.Color;

import javax.swing.JFrame;


public class Main{

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//init objects
		P2i[] ov1 = new P2i[]{new P2i(-5, -7), new P2i(-4,3), new P2i(15, 2), new P2i(14, -6)};
		P2i[] ov2 = new P2i[]{new P2i(-2, 6), new P2i(4,6), new P2i(4, -6), new P2i(-2, -6)};
		P2i[] ov3 = new P2i[]{new P2i(0,0), new P2i(80, 0), new P2i(80, -10), new P2i(0, -10)};
		P2i[] ov4 = new P2i[]{new P2i(17,84), new P2i(74, 84), new P2i(88, 34), new P2i(45, 4), new P2i(2,34)};
		for(int i = 0; i <ov1.length; i++) ov1[i].scale(1.3);
		for(int i = 0; i <ov2.length; i++) ov2[i].scale(1.3);
		for(int i = 0; i <ov3.length; i++) ov3[i].scale(1.6);
		for(int i = 0; i <ov4.length; i++) ov4[i].scale(0.4);
		//var draw_list = [];

		
		LScene scene = new LScene();
		scene.add_object(ov1, false, new P2i(35, 30));
		scene.add_object(ov1, false, new P2i(86, 30), new P2i(5,-.5), 8/57.3);
		scene.add_object(ov4, false, new P2i(280, 95), new P2i(-1,-2), 8/57.3);
		scene.add_object(ov2, false, new P2i(380, 40), new P2i(-4.5,-2.5), 2/57.3);
		scene.add_object(ov4, false, new P2i(380, 100), new P2i(-3,-4.5), 2/57.3);
		scene.add_object(ov2, false, new P2i(230, 100), new P2i(3,-4.5), -1/57.3);
		scene.add_object(ov2, false, new P2i(66.66, 30), new P2i(0,0), -5.2/57.4);
		scene.add_object(ov2, false, new P2i(35, 160), new P2i(10,-2), -3/57.4);
		scene.add_object(ov2, false, new P2i(260, 185), new P2i(-10,-3.0), -3/57.4);
		scene.add_object(ov1, true, new P2i(22, 100));
		scene.add_object(ov1, true, new P2i(60, 100));
		scene.add_object(ov3, true, new P2i(360, 160), -45/57.3);
	
		
		//end
		
		JFrame frame = new JFrame();
		LGraphics g = new LGraphics(800,500);
		g.set_scene(scene);
		frame.setSize(800, 400);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.add(g);
		frame.setVisible(true);
		
		while(true)
		{
			scene.step(0.05);
			scene.apply_physics();
			
			g.repaint();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
