//JS code by Luke Lincoln

//the primary object
function ll_LG(canvas_id, width, height)
{
	this.canvas_id = canvas_id;
	this.canvas = document.getElementById(this.canvas_id);
	this.context = this.canvas.getContext('2d');
	this.width = width;
	this.height = height;
};

ll_LG.prototype.in_range = function(x, y)
{
	return x >= 0 && x < this.width && y >= 0 && y < this.height;
};

ll_LG.prototype.in_rangev = function(p)
{
	return this.in_range(p.x, p.y);
};

ll_LG.prototype.in_rangetd = function(x, y)
{
	return x >= 0 && x < this.width && y < this.height;
};

ll_LG.prototype.in_rangetdv = function(p)
{
	return this.in_rangetd(p.x, p.y);
};

ll_LG.prototype.clear = function(col)
{
	if(col == undefined) col = 'black';
	this.rectf(0,0,this.width, this.height, col);
};


ll_LG.prototype.line = function(x1, y1, x2, y2, col)
{
	this.context.beginPath();
	this.context.moveTo(x1, y1);
	this.context.lineTo(x2, y2);
	this.context.strokeStyle = col;
	this.context.stroke();
};

ll_LG.prototype.linev = function(p1, p2, col)
{
	this.context.beginPath();
	this.context.moveTo(p1.x, p1.y);
	this.context.lineTo(p2.x, p2.y);
	this.context.strokeStyle = col;
	this.context.stroke();
};

ll_LG.prototype.rect = function(x, y, w, h, col)
{
	this.context.beginPath();
	this.context.rect(x, y, w, h);
	this.context.lineWidth = 1;
	this.context.strokeStyle = col;
	this.context.stroke();
};

ll_LG.prototype.rectf = function(x, y, w, h, col)
{
	this.context.beginPath();
	this.context.rect(x, y, w, h);
	this.context.lineWidth = 1;
	this.context.fillStyle = col;
	this.context.fill();
};

ll_LG.prototype.circ = function(x, y, r, col)
{
	this.context.beginPath();
	this.context.arc(x, y, r, 0, 2 * Math.PI, false);
	context.lineWidth = 1;
	context.strokeStyle = col;
	context.stroke();
};

ll_LG.prototype.draw_shape = function(ob, col)
{
	var rv = false;
	if(ob.points.length == 0) return rv;
	var i = 0;
	for(i = 0; i < ob.points.length; i++)
	{
		var j = (i+1) % ob.points.length;
		this.line(ob.points[i].x, ob.points[i].y, ob.points[j].x, ob.points[j].y, col);
		rv = rv || this.in_rangetdv(ob.points[i]);
	}
	var avg = ob.get_avg();
	this.linev(avg, ob.points[0], col);
	return rv;
};

ll_LG.prototype.draw_object = function(ob, col1, col2)
{
	var shape = ob.get_shape();
	var bbox = shape.get_bbox();
	this.draw_bbox(bbox, col2);
	return this.draw_shape(shape, col1);
};

ll_LG.prototype.draw_bbox = function(bb, col)
{
	this.rect(bb.mn.x, bb.mn.y, bb.mx.x-bb.mn.x, bb.mx.y-bb.mn.y, col);
};