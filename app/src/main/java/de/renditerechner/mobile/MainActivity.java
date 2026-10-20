package de.renditerechner.mobile;
import android.app.*;import android.os.*;import android.graphics.*;import android.content.*;import android.view.*;import android.widget.*;import android.text.*;import android.graphics.drawable.GradientDrawable;import android.graphics.Typeface;import org.json.*;import java.util.*;
public class MainActivity extends Activity {
 final int NAVY=0xff142F49,BLUE=0xff216386,TEAL=0xff168B83,BG=0xffF3F7FA,BORDER=0xffDEE7ED,MUTED=0xff6F8494,WHITE=0xffFFFFFF;LinearLayout section;TextView statInvest,statGain,statEnd; LinearLayout root,body; EditText start,monthly,years,rate,target,age,goalAge,tax,free,inflation; Spinner mode; Switch advance,taxOn,inflOn,begin; TextView output,details; SharedPreferences prefs; int page=0;
 final String[] MODES={"Endkapital","Startkapital","Monatliche Sparrate","Spardauer","Jahresrendite","Zielvermögen"};
 public void onCreate(Bundle b){super.onCreate(b);prefs=getSharedPreferences("rendite_neu",0);show();}
 int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
 TextView label(String s,int size){TextView v=new TextView(this);v.setText(s);v.setTextColor(NAVY);v.setTextSize(size);v.setPadding(dp(2),dp(7),dp(2),dp(6));return v;}
 
 GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 GradientDrawable gradient(int a,int b,int radius){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{a,b});d.setCornerRadius(dp(radius));return d;}
 LinearLayout panel(boolean dark){LinearLayout c=column();c.setPadding(dp(18),dp(16),dp(18),dp(16));c.setBackground(dark?gradient(NAVY,BLUE,20):shape(WHITE,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(15);body.addView(c,p);if(!dark)c.setElevation(dp(2));return c;}
 TextView styled(String text,int size,int color,boolean bold){TextView v=new TextView(this);v.setText(text);v.setTextSize(size);v.setTextColor(color);if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
 void heading(LinearLayout p,String title,String subtitle){p.addView(styled(title,19,NAVY,true));TextView sub=styled(subtitle,13,MUTED,false);sub.setPadding(0,dp(3),0,dp(13));p.addView(sub);}
 Button button(String value,boolean filled){Button b=new Button(this);b.setText(value);b.setAllCaps(false);b.setTextSize(14);b.setTextColor(filled?WHITE:BLUE);b.setBackground(filled?gradient(BLUE,TEAL,13):shape(0xffE8F1F5,13));return b;}
 TextView metric(LinearLayout parent,String title){LinearLayout c=column();c.setPadding(dp(9),dp(9),dp(7),dp(9));c.setBackground(shape(0x17FFFFFF,13));c.addView(styled(title,11,0xffDCEFF3,false));TextView v=styled("–",14,WHITE,true);v.setPadding(0,dp(4),0,0);c.addView(v);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);if(parent.getChildCount()>0)p.leftMargin=dp(6);parent.addView(c,p);return v;}

 void show(){
 root=column();root.setBackgroundColor(BG);
 LinearLayout header=column();header.setPadding(dp(18),dp(17),dp(18),dp(16));header.setBackground(gradient(NAVY,BLUE,0));
 header.addView(styled("Renditerechner",25,WHITE,true));TextView tagline=styled("Deine Ziele. Dein Vermögen.",13,0xffD0E4EE,false);tagline.setPadding(0,dp(5),0,0);header.addView(tagline);root.addView(header);
 ScrollView scroll=new ScrollView(this);scroll.setFillViewport(false);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
 body=column();body.setPadding(dp(14),dp(16),dp(14),dp(18));scroll.addView(body);
 section=null;output=null;details=null;statInvest=null;statGain=null;statEnd=null;
 if(page==0)calculator();else if(page==1)analysis();else scenarios();
 LinearLayout nav=new LinearLayout(this);nav.setGravity(Gravity.CENTER_VERTICAL);nav.setBackgroundColor(WHITE);nav.setPadding(dp(9),dp(6),dp(9),dp(9));
 String[] titles={"Rechner","Auswertung","Szenarien"};
 for(int i=0;i<3;i++){final int index=i;TextView b=styled(titles[i],13,i==page?TEAL:MUTED,i==page);b.setGravity(Gravity.CENTER);b.setPadding(dp(3),dp(14),dp(3),dp(14));if(i==page)b.setBackground(shape(0xffE9F5F4,13));nav.addView(b,new LinearLayout.LayoutParams(0,dp(48),1));b.setOnClickListener(v->{if(page==0)remember();page=index;show();});}
 root.addView(nav);setContentView(root);
 }
 EditText field(String name,String key,String def){
 LinearLayout wrapper=column();
 TextView l=styled(name,13,MUTED,false);l.setPadding(0,dp(7),0,dp(6));wrapper.addView(l);
 EditText e=new EditText(this);e.setSingleLine(true);e.setTextColor(NAVY);e.setTextSize(17);e.setSelectAllOnFocus(true);e.setPadding(dp(13),dp(9),dp(13),dp(9));e.setBackground(shape(0xffF4F8FA,12));e.setInputType(8194|4096);e.setText(prefs.getString(key,def));wrapper.addView(e,new LinearLayout.LayoutParams(-1,dp(49)));
 LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(7);(section==null?body:section).addView(wrapper,p);
 e.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int d){}public void onTextChanged(CharSequence s,int st,int before,int count){refresh();}public void afterTextChanged(Editable ed){}});
 return e;
 }
 Switch toggle(String name,String key,boolean def){
 Switch s=new Switch(this);s.setText(name);s.setTextSize(14);s.setTextColor(NAVY);s.setPadding(0,dp(8),0,dp(9));s.setChecked(prefs.getBoolean(key,def));(section==null?body:section).addView(s);s.setOnCheckedChangeListener((b,v)->refresh());return s;
 }
 double num(EditText e){return Double.parseDouble(e.getText().toString().trim().replace(" ","").replace(",","."));}
 String money(double a){return String.format(Locale.GERMANY,"%,.2f €",a);}
 void calculator(){
 LinearLayout resultCard=panel(true);resultCard.addView(styled("DEINE BERECHNUNG",12,0xffD2E6F0,true));resultCard.addView(styled("Prognostiziertes Ergebnis",13,0xffD2E6F0,false));
 output=styled("–",31,WHITE,true);output.setPadding(0,dp(5),0,dp(3));resultCard.addView(output);
 details=styled("",12,0xffD3E8EE,false);resultCard.addView(details);LinearLayout stats=new LinearLayout(this);stats.setGravity(Gravity.CENTER_VERTICAL);stats.setPadding(0,dp(13),0,0);
 statInvest=metric(stats,"Einzahlungen");statGain=metric(stats,"Erträge");statEnd=metric(stats,"Nach Steuern");resultCard.addView(stats);
 LinearLayout selection=panel(false);heading(selection,"Berechnungsmodus","Welche Größe möchtest du berechnen?");
 mode=new Spinner(this);ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,MODES);mode.setAdapter(adapter);mode.setSelection(prefs.getInt("mode",0));selection.addView(mode,new LinearLayout.LayoutParams(-1,dp(50)));
 mode.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){}public void onItemSelected(android.widget.AdapterView<?> p,View v,int pos,long id){if(pos!=prefs.getInt("mode",0)){remember();prefs.edit().putInt("mode",pos).apply();show();}else refresh();}});
 LinearLayout entries=panel(false);heading(entries,"Deine Eckdaten","Alle Werte sind direkt veränderbar.");
 section=entries;
 start=field("Startkapital (€)","start","100000");
 monthly=field("Monatliche Sparrate (€)","monthly","2000");
 years=field("Spardauer (Jahre)","years","15");
 rate=field("Erwartete Rendite pro Jahr (%)","rate","6");
 LinearLayout goals=panel(false);heading(goals,"Zielwerte","Für Rückwärts- und Zielvermögensrechnungen.");
 section=goals;
 target=field("Gewünschtes Zielkapital (€)","target","1500000");
 age=field("Dein aktuelles Alter","age","43");
 goalAge=field("Gewünschtes Zielalter","goalAge","55");
 LinearLayout options=panel(false);heading(options,"Erweiterte Optionen","Steuern, Inflation und Einzahlungszeitpunkt.");
 section=options;
 advance=toggle("Expertenmodus","expert",false);
 begin=toggle("Einzahlung am Monatsanfang","begin",false);
 taxOn=toggle("Steuern schätzen","taxOn",false);
 inflOn=toggle("Inflation berücksichtigen","inflOn",false);
 tax=field("Vereinfachter Steuersatz (%)","tax","26.375");
 free=field("Freibetrag (€)","free","1000");
 inflation=field("Inflationsrate pro Jahr (%)","inflation","2");
 section=null;
 // Unneeded fields remain available to the calculation and persistence, but are collapsed.
 // Keep view structure stable while switching options.
 advance.setOnCheckedChangeListener((b,v)->{options.setVisibility(View.VISIBLE);tax.getParent().setVisibility(v?View.VISIBLE:View.GONE);free.getParent().setVisibility(v?View.VISIBLE:View.GONE);inflation.getParent().setVisibility(v?View.VISIBLE:View.GONE);refresh();});
 boolean ex=advance.isChecked();tax.getParent().setVisibility(ex?View.VISIBLE:View.GONE);free.getParent().setVisibility(ex?View.VISIBLE:View.GONE);inflation.getParent().setVisibility(ex?View.VISIBLE:View.GONE);
 LinearLayout actions=panel(false);heading(actions,"Deine Szenarien","Speichere deine Berechnung oder öffne die Auswertung.");
 Button save=button("Szenario speichern",true);actions.addView(save,new LinearLayout.LayoutParams(-1,dp(50)));
 save.setOnClickListener(v->{remember();EditText input=new EditText(this);input.setHint("Name des Szenarios");new AlertDialog.Builder(this).setTitle("Szenario speichern").setView(input).setPositiveButton("Speichern",(d,w)->save(input.getText().toString())).setNegativeButton("Abbrechen",null).show();});
 Button graph=button("Diagramm & Jahrestabelle ansehen",false);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(50));p.topMargin=dp(9);actions.addView(graph,p);graph.setOnClickListener(v->{remember();page=1;show();});
 refresh();
 }
 void remember(){if(start==null)return;SharedPreferences.Editor p=prefs.edit();EditText[] es={start,monthly,years,rate,target,age,goalAge,tax,free,inflation};String[] keys={"start","monthly","years","rate","target","age","goalAge","tax","free","inflation"};for(int i=0;i<es.length;i++)p.putString(keys[i],es[i].getText().toString());p.putInt("mode",mode.getSelectedItemPosition());p.putBoolean("expert",advance.isChecked());p.putBoolean("begin",begin.isChecked());p.putBoolean("taxOn",taxOn.isChecked());p.putBoolean("inflOn",inflOn.isChecked());p.apply();}
 static double fv(double p,double a,int n,double annual,boolean begin){double r=Math.expm1(Math.log1p(annual/100)/12);double g=Math.pow(1+r,n);double factor=Math.abs(r)<1e-12?n:(g-1)/r;return p*g+a*factor*(begin?1+r:1);}
 static double solveRate(double p,double a,int n,double target,boolean begin){double lo=-99.9,hi=1000;for(int i=0;i<90;i++){double mid=(lo+hi)/2;if(fv(p,a,n,mid,begin)<target)lo=mid;else hi=mid;}return (lo+hi)/2;}
 static class Result{double p,a,rate,end,paid,tax,real;int n;}
 Result calc(){double p=num(start),a=num(monthly),y=num(years),r=num(rate),goal=num(target);if(p<0||a<0||y<0||r<=-100||r>1000||goal<0)throw new ExceptionInInitializerError("Werte außerhalb des zulässigen Bereichs");int n=(int)Math.round(y*12),m=mode.getSelectedItemPosition();boolean beg=begin.isChecked();if(m==5){n=((int)num(goalAge)-(int)num(age))*12;if(n<=0)throw new IllegalArgumentException("Zielalter muss größer als aktuelles Alter sein");m=2;}if(n<0||n>1200)throw new IllegalArgumentException("Laufzeit maximal 100 Jahre");double base=fv(p,0,n,r,beg),unit=fv(0,1,n,r,beg);
 if(m==1){double g=Math.pow(1+r/100,n/12.0);p=(goal-a*unit)/g;}
 if(m==2){if(unit==0)throw new IllegalArgumentException("Laufzeit muss größer als null sein");a=(goal-base)/unit;}
 if(m==3){if(p+a==0)throw new IllegalArgumentException("Kein Kapital und keine Sparrate");int k=0;while(k<1200&&fv(p,a,k,r,beg)<goal)k++;if(k==1200&&fv(p,a,k,r,beg)<goal)throw new IllegalArgumentException("Ziel in 100 Jahren nicht erreichbar");n=k;}
 if(m==4){if(n==0)throw new IllegalArgumentException("Laufzeit muss größer als null sein");r=solveRate(p,a,n,goal,beg);}
 if(p<0||a<0||!Double.isFinite(p+a+r))throw new IllegalArgumentException("Ziel unter diesen Annahmen nicht erreichbar");Result z=new Result();z.p=p;z.a=a;z.n=n;z.rate=r;z.end=fv(p,a,n,r,beg);z.paid=p+a*n;double profit=Math.max(0,z.end-z.paid);z.tax=taxOn.isChecked()?Math.max(0,profit-Math.max(0,num(free)))*num(tax)/100:0;z.real=(z.end-z.tax)/(inflOn.isChecked()?Math.pow(1+num(inflation)/100,n/12.0):1);return z;}
 void refresh(){
 if(output==null||page!=0||start==null||monthly==null||years==null||rate==null||target==null||age==null||goalAge==null||tax==null||free==null||inflation==null)return;
 try{
 Result z=calc();int m=mode.getSelectedItemPosition();
 String lead="Endkapital";String value=money(z.end);
 if(m==1){lead="Benötigtes Startkapital";value=money(z.p);}
 if(m==2||m==5){lead="Benötigte monatliche Sparrate";value=money(z.a);}
 if(m==3){lead="Benötigte Spardauer";value=String.format(Locale.GERMANY,"%.1f Jahre",z.n/12.0);}
 if(m==4){lead="Benötigte Jahresrendite";value=String.format(Locale.GERMANY,"%.2f %%",z.rate);}
 output.setText(value);details.setText(lead+" · "+String.format(Locale.GERMANY,"%.2f %% p.a.",z.rate));
 statInvest.setText(money(z.paid));statGain.setText(money(z.end-z.paid));statEnd.setText(money(z.end-z.tax));
 }catch(Exception|ExceptionInInitializerError e){output.setText("Eingaben prüfen");details.setText(e.getMessage()==null?"Bitte Werte prüfen":e.getMessage());statInvest.setText("–");statGain.setText("–");statEnd.setText("–");}
 }
 void save(String name){try{JSONArray saved=new JSONArray(prefs.getString("saved","[]"));JSONObject j=new JSONObject();j.put("name",name.trim().isEmpty()?"Szenario "+(saved.length()+1):name.trim());String[] keys={"start","monthly","years","rate","target","age","goalAge","tax","free","inflation"};for(String key:keys)j.put(key,prefs.getString(key,""));j.put("mode",prefs.getInt("mode",0));saved.put(j);prefs.edit().putString("saved",saved.toString()).apply();Toast.makeText(this,"Gespeichert",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Speichern fehlgeschlagen",Toast.LENGTH_SHORT).show();}}
 void scenarios(){
 LinearLayout heading=panel(false);heading(heading,"Gespeicherte Szenarien","Öffne eine Berechnung mit einem Fingertipp.");
 try{JSONArray arr=new JSONArray(prefs.getString("saved","[]"));if(arr.length()==0){heading.addView(styled("Noch keine Szenarien gespeichert.",14,MUTED,false));return;}
 for(int i=arr.length()-1;i>=0;i--){JSONObject j=arr.getJSONObject(i);LinearLayout c=panel(false);c.addView(styled(j.optString("name","Szenario"),17,NAVY,true));String s="Start: "+j.optString("start")+" €     Monatlich: "+j.optString("monthly")+" €";TextView caption=styled(s,12,MUTED,false);caption.setPadding(0,dp(9),0,dp(10));c.addView(caption);Button b=button("Szenario öffnen",false);c.addView(b,new LinearLayout.LayoutParams(-1,dp(46)));b.setOnClickListener(v->{SharedPreferences.Editor ed=prefs.edit();String[] keys={"start","monthly","years","rate","target","age","goalAge","tax","free","inflation"};for(String key:keys)ed.putString(key,j.optString(key,prefs.getString(key,"")));ed.putInt("mode",j.optInt("mode",0));ed.apply();page=0;show();});}
 }catch(Exception e){heading.addView(styled("Szenarien konnten nicht geladen werden.",14,MUTED,false));}
 }
 void analysis(){
 LinearLayout intro=panel(false);heading(intro,"Vermögensentwicklung","Prognose mit konstanter jährlicher Rendite.");
 double p=read("start",100000),a=read("monthly",2000),r=read("rate",6),y=read("years",15);int n=(int)Math.round(y*12);boolean beg=prefs.getBoolean("begin",false);
 if(n<0||n>1200||r<=-100){intro.addView(styled("Bitte die Eingaben im Rechner prüfen.",14,MUTED,false));return;}
 double total=fv(p,a,n,r,beg),paid=p+a*n;intro.addView(styled(money(total),29,BLUE,true));intro.addView(styled("Einzahlungen: "+money(paid)+"   ·   Ertrag: "+money(total-paid),14,TEAL,true));
 LinearLayout graphCard=panel(false);heading(graphCard,"Vermögenskurve","Blau: Vermögen  ·  Petrol: Einzahlungen");
 graphCard.addView(new Graph(p,a,n,r,beg),new LinearLayout.LayoutParams(-1,dp(210)));
 LinearLayout comparisons=panel(false);heading(comparisons,"Renditevergleich","Mit 2 Prozentpunkten Abstand zum Basisszenario.");
 for(double diff:new double[]{-2,0,2}){double v=r+diff;if(v<=-100)continue;LinearLayout l=new LinearLayout(this);l.setGravity(Gravity.CENTER_VERTICAL);TextView title=styled(String.format(Locale.GERMANY,"%.1f %% p.a.",v),15,diff==0?TEAL:NAVY,diff==0);TextView val=styled(money(fv(p,a,n,v,beg)),15,NAVY,true);l.addView(title,new LinearLayout.LayoutParams(0,-2,1));l.addView(val);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(49));comparisons.addView(l,lp);}
 LinearLayout table=panel(false);heading(table,"Jahresübersicht","Kapital, Einzahlungen und Ertrag im Zeitverlauf.");
 for(int i=0;i<=n;i+=12){double z=fv(p,a,i,r,beg);LinearLayout line=column();line.setPadding(dp(11),dp(10),dp(11),dp(10));line.setBackground(shape(0xffF4F8FA,12));line.addView(styled("Jahr "+(i/12)+"  ·  "+money(z),14,NAVY,true));line.addView(styled("Eigenes Geld: "+money(p+a*i)+"   Ertrag: "+money(z-p-a*i),12,MUTED,false));LinearLayout.LayoutParams q=new LinearLayout.LayoutParams(-1,-2);q.bottomMargin=dp(7);table.addView(line,q);}
 if(n%12!=0)table.addView(styled("Endwert: "+money(total),14,TEAL,true));
 LinearLayout notice=panel(false);notice.addView(styled("Modellrechnung bei konstanter effektiver Jahresrendite. Keine Renditegarantie.",12,MUTED,false));
 }
 double read(String key,double fallback){try{return Double.parseDouble(prefs.getString(key,""+fallback).replace(",","."));}catch(Exception e){return fallback;}}
 class Graph extends View{
 double p,a,r;int n;boolean b;Paint paint=new Paint(3);
 Graph(double p,double a,int n,double r,boolean b){super(MainActivity.this);this.p=p;this.a=a;this.n=n;this.r=r;this.b=b;}
 protected void onDraw(Canvas c){super.onDraw(c);float left=dp(11),right=getWidth()-dp(11),top=dp(12),bottom=getHeight()-dp(27);double max=Math.max(1,Math.max(fv(p,a,n,r,b),p+a*n)*1.05);
 for(int l=0;l<=4;l++){float y=top+(bottom-top)*l/4;paint.setColor(BORDER);paint.setStrokeWidth(dp(1));c.drawLine(left,y,right,y,paint);}
 for(int k=0;k<2;k++){Path path=new Path();for(int i=0;i<=90;i++){int month=(n*i)/90;double val=k==0?fv(p,a,month,r,b):p+a*month;float x=left+(right-left)*i/90f,y=bottom-(float)(val/max)*(bottom-top);if(i==0)path.moveTo(x,y);else path.lineTo(x,y);}paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(dp(3));paint.setColor(k==0?BLUE:TEAL);c.drawPath(path,paint);}
 paint.setStyle(Paint.Style.FILL);paint.setColor(MUTED);paint.setTextSize(dp(12));c.drawText("Start",left,bottom+dp(18),paint);String time=String.format(Locale.GERMANY,"%.1f Jahre",n/12.0);c.drawText(time,right-paint.measureText(time),bottom+dp(18),paint);
 }
 }
}