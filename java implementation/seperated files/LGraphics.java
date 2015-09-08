import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;


public class LGraphics extends JPanel
{
	int width, height, offsetx, offsety;
	LScene scene;
	LGraphics(int w, int h)
	{
		scene = null;
		this.width = w;
		this.height = h;
		this.setDoubleBuffered(true);
	}
	
	void set_scene(LScene scene)
	{
		this.scene = scene;
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		clear(g, Color.white);
		g.setColor(Color.black);
		g.drawString(new String("number of objects:" + scene.objects.size()), this.width-300, 20);
		//line(g, 0.0, 150.0, Math.random()*399.0, 150.0, Color.red);
		if(scene != null)
		{
			boolean ar[] = new boolean[scene.objects.size()];
			for(int i = 0; i < scene.objects.size(); i++)
			{
				ar[i] = this.draw_object(g, scene.objects.get(i), Color.blue, new Color(242,240,242));
			}
			for(int i = 0; i < scene.objects.size(); i++)
			{
				if(!ar[i])
				{
					scene.objects.remove(i);
					break;
				}
			}
		}
		repaint();
	}
	
	boolean in_range(double x, double y)
	{
		return x >= 0 && x < this.width && y >= 0 && y < this.height;
	}

	boolean in_rangev(P2i p)
	{
		return this.in_range(p.x, p.y);
	}

	boolean in_rangetd(double x, double y)
	{
		return x >= 0 && x < this.width && y < this.height;
	}

	boolean in_rangetdv(P2i p)
	{
		return this.in_rangetd(p.x, p.y);
	}

	void clear(Graphics g, Color c)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(c);
		gd.fillRect(0, 0, this.width, this.height);
	}


	void line(Graphics g, double x1, double y1, double x2, double y2, Color col)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(col);
		gd.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
	}
	
	void linev(Graphics g, P2i p1, P2i p2, Color col)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(col);
		gd.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
	}
	
	void rect(Graphics g, double x, double y, double w, double h, Color col)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(col);
		gd.drawRect((int)x, (int)y, (int)w, (int)h);
	}
	void rectf(Graphics g, double x, double y, double w, double h, Color col)
	{
		Graphics2D gd = (Graphics2D) g;
		gd.setPaint(col);
		gd.fillRect((int)x, (int)y, (int)w, (int)h);
	}
	
	public boolean draw_shape(Graphics g, LShape ob, Color col)
	{
		boolean rv = false;
		if(ob.points.length == 0) return rv;
		for(int i = 0; i < ob.points.length; i++)
		{
			int j = (i+1) % ob.points.length;
			this.line(g, ob.points[i].x, ob.points[i].y, ob.points[j].x, ob.points[j].y, col);
			rv = rv || this.in_rangetdv(ob.points[i]);
		}
		P2i avg = ob.get_avg();
		this.linev(g, avg, ob.points[0], col);
		return rv;
	}

	public boolean draw_object(Graphics g, LObj ob, Color col1, Color col2)
	{
		LShape shape = ob.get_shape();
		Bbox bbox = shape.get_bbox();
		this.draw_bbox(g, bbox, col2);
		return this.draw_shape(g, shape, col1);
	}

	public void draw_bbox(Graphics g, Bbox bb, Color col)
	{
		this.rect(g, bb.mn.x, bb.mn.y, bb.mx.x-bb.mn.x, bb.mx.y-bb.mn.y, col);
	}
	
	
}
