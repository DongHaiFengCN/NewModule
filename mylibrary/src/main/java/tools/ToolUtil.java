package tools;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：dlcache
 * 类描述：
 * 创建人：donghaifeng
 * 创建时间：2018/1/15 13:04
 * 修改人：donghaifeng
 * 修改时间：2018/1/15 13:04
 * 修改备注：
 */

public class ToolUtil {
    public static String[][] pinyin2sz = new String[][]{{"a", "b", "c", ""}, {"d", "e", "f", ""}, {"g", "h", "i", ""}, {"j", "k", "l", ""}, {"m", "n", "o", ""}, {"p", "q", "r", "s"}, {"t", "u", "v", ""}, {"w", "x", "y", "z"}};
    static HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = new int[2];

            //返回当前view的距离屏幕左侧与距离屏幕顶部的距离
            v.getLocationInWindow(l);


            //计算view 所在的位置范围
            int left = l[0];
            int top = l[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();

            // 点击的位置在edit之内忽略掉
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {

                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }


    /**
     * 复制文件
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    public static boolean copyFile(String oldPath, String newPath) {

        boolean flag = true;
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();

        }

        return flag;
    }


    /**
     * 打开约定的图片缓存文件夹，不存在就创建一个
     *
     * @param url
     */
    public static void openFileDir(String url) {
        File file1 = new File(url);
        if (!file1.exists() && !file1.isDirectory()) {
            file1.mkdir();
        }
    }
    /**
     * @param pinyin
     * @return
     */
    public static String ChangeSZ(String pinyin) {
        Log.e("changesz", "ss=" + pinyin);
        String SZ = "";
        for (int i = 0; i < pinyin.length(); i++) {
            for (int j = 2; j < 10; j++) {
                for (int k = 0; k < 4; k++) {
                    if (pinyin2sz[j - 2][k].equals(pinyin.charAt(i) + "")) {
                        SZ += Integer.toString(j);
                    }
                }
            }
        }
        Log.e("ChangeSZ", "sz=" + SZ);
        return SZ;
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {
        char[] arr;
        StringBuffer pybf = new StringBuffer();
        arr = chinese.toCharArray();

        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        // defaultFormat=null;

        return pybf.toString().replaceAll("\\W", "").trim();
    }

    public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String uuidStr = uuid.toString();
        return uuidStr;
    }
  /*  private void add26Code()
    {
        List<Document> documentList = CDBHelper.getDocmentsByClass(getApplicationContext(),DishesC.class);
        for(Document doc:documentList)
        {
            String name26 = doc.getString("dishesNameCode26");
            if(TextUtils.isEmpty(name26))
            {

                String name = doc.getString("dishesName");
                Log.e("dishesName","name----->"+name);
                name26 =    Tool.getFirstSpell(name);
                Log.e("dishesName26","name26----->"+name26);
                doc.setString("dishesNameCode26",name26);
                CDBHelper.saveDocument(getApplicationContext(),doc);
            }
            else
                continue;

        }
    }*/

    /**
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式（表情占4个字节，需要utf8mb4字符集）
     * @param str
     * 待转换字符串
     * @return 转换后字符串
     * @throws UnsupportedEncodingException
     * exception
     */
    public static String emojiConvert1(String str)
            throws UnsupportedEncodingException {
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(
                        sb,
                        "[["
                                + URLEncoder.encode(matcher.group(1),
                                "UTF-8") + "]]");
            } catch(UnsupportedEncodingException e) {
                throw e;
            }
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     * @param str
     * 转换后的字符串
     * @return 转换前的字符串
     * @throws UnsupportedEncodingException
     * exception
     */
    public static String emojiRecovery2(String str)
            throws UnsupportedEncodingException {
        String patternString = "\\[\\[(.*?)\\]\\]";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            try {
                matcher.appendReplacement(sb,
                        URLDecoder.decode(matcher.group(1), "UTF-8"));
            } catch(UnsupportedEncodingException e) {
                throw e;
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
