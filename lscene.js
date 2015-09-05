//JS code by Luke Lincoln

function LScene()
{
	this.objects = [];
	this.num_integrates = 10;
}

LScene.prototype.add_object = function(pnts, is_fixed, props)
{
	this.objects.push(new LObj(pnts, is_fixed, props));
};

LScene.prototype.step = function(t)
{
	var i = 0, j;
	var td = t / this.num_integrates;
	for(; i < this.objects.length; i++)
		for(j = 0; j < this.num_integrates; j++)
			this.objects[i].step(td);
};

LScene.prototype.apply_physics = function()
{
	 var i = 0, j;
	 for(; i < this.objects.length-1; i++)
	 {
		 for(j = i+1; j < this.objects.length; j++)
		 {
			var ob1 = this.objects[i];
			var ob2 = this.objects[j];
			if(ob1.fixed && ob2.fixed) continue;
			var s1 = ob1.get_shape();
			var s2 = ob2.get_shape();
			var col = s1.collision_detection(s2);
			//for collision i need: 
			/*
				whether there was a collision:
				a list of collisions:
					the point of collision
					the penetration or distance
					the normal of the surface involved
			*/
			if(col.collision)
			{
				var k = 0;
				var n = col.list.length;
				for(; k < n; k++)
				{
					ll_pos_correct(ob1, ob2, col.list[k].dist, col.list[k].norm.clone().inv());
					ll_resolve_collision(ob1, ob2, col.list[k].norm.clone().inv(), col.list[k].point.clone(), n);
				}
				
			}
		 }
	 }
};



