import java.util.ArrayList;


public class LScene {


	ArrayList<LObj> objects;
	int num_integrates;
	
	public LScene()
	{
		this.objects = new ArrayList<LObj>();
		this.num_integrates = 100;
	}
	
	public void add_object(P2i[] pnts, boolean is_fixed)
	{
		this.objects.add(new LObj(pnts, is_fixed));
	}
	
	public void add_object(P2i[] pnts, boolean is_fixed, P2i pos)
	{
		
		LObj ob = new LObj(pnts, is_fixed);
		ob.pos = pos.cpy();
		this.objects.add(ob);
	}
	
	public void add_object(P2i[] pnts, boolean is_fixed, P2i pos, P2i vel, double ang)
	{
		LObj ob = new LObj(pnts, is_fixed);
		ob.pos = pos.cpy();
		ob.vel = vel.cpy();
		ob.ang_vel = ang;
		this.objects.add(ob);
	}
	
	public void add_object(P2i[] pnts, boolean is_fixed, P2i pos, double orient)
	{
		LObj ob = new LObj(pnts, is_fixed);
		ob.pos = pos.cpy();
		ob.orient = orient;
		
		this.objects.add(ob);
	}
	
	public void step(double t)
	{
		double td = t / (double)this.num_integrates;
		for(int i = 0; i < this.objects.size(); i++)
			for(int j = 0; j < this.num_integrates; j++)
				this.objects.get(i).step(td);
	}
	
	public void apply_physics()
	{
		 for(int i = 0; i < this.objects.size()-1; i++)
		 {
			 for(int j = i+1; j < this.objects.size(); j++)
			 {
				LObj ob1 = this.objects.get(i);
				LObj ob2 = this.objects.get(j);
				if(ob1.fixed && ob2.fixed) continue;
				LShape s1 = ob1.get_shape();
				LShape s2 = ob2.get_shape();
				ArrayList<Collision> col = s1.collision_detection(s2);
				//for collision i need: 
				/*
					whether there was a collision:
					a list of collisions:
						the point of collision
						the penetration or distance
						the normal of the surface involved
				*/
				if(col.size() > 0)
				{
					double n = (double)col.size();
					for(int k = 0; k < n; k++)
					{
						LObj.ll_pos_correct(ob1, ob2, col.get(k).dist, col.get(k).norm.cpy().inv());
						LObj.ll_resolve_collision(ob1, ob2, col.get(k).norm.cpy().inv(), col.get(k).point.cpy(), n);
					}
					
				}
			 }
		 }
	}

	
}
