package com.javaoffers.base.eslog.anno;

import com.javaoffers.base.eslog.conf.AutoConfigEslog;
import org.springframework.context.annotation.Import;

/**
 * @Description: eslog启动类
 * @Auther: create by cmj on 2020/10/30 10:15
 */
@Import(AutoConfigEslog.class)
public class EnableEslog {
}
