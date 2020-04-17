package huaiye.com.vim.ui.meet.basemodel;

/**
 * Created by Administrator on 2018\3\5 0005.
 */

public class SelectedModel<T> {
    public boolean isChecked;
    public T bean;

    public SelectedModel(T temp) {
        bean = temp;
    }
}
