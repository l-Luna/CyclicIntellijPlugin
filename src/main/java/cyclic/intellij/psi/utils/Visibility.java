package cyclic.intellij.psi.utils;

import com.intellij.lang.jvm.JvmClass;
import com.intellij.lang.jvm.JvmMember;
import com.intellij.psi.util.PsiUtil;

import java.util.Objects;

import static com.intellij.lang.jvm.JvmModifier.*;
import static cyclic.intellij.psi.utils.JvmClassUtils.getPackageName;
import static cyclic.intellij.psi.utils.JvmClassUtils.isClassAssignableTo;

public class Visibility{
	
	public static boolean visibleFrom(JvmMember member, JvmMember from){
		if(from == null)
			return false;
		if(member.hasModifier(PUBLIC))
			return true;
		
		var inCls = inOrSelf(member);
		var fromCls = inOrSelf(from);
		
		if(member.hasModifier(PACKAGE_LOCAL))
			return getPackageName(inCls).equals(getPackageName(fromCls));
		
		if(member.hasModifier(PROTECTED))
			return getPackageName(inCls).equals(getPackageName(fromCls))
					|| isClassAssignableTo(fromCls, inCls);
		
		if(member.hasModifier(PRIVATE))
			return Objects.equals(inCls.getQualifiedName(), fromCls.getQualifiedName());
		
		return false;
	}
	
	private static JvmClass inOrSelf(JvmMember member){
		JvmClass in = member.getContainingClass();
		if(in == null)
			return (JvmClass)member;
		return in;
	}
	
	@PsiUtil.AccessLevel
	public static int getVisibilityLevel(JvmMember member){
		if(member.hasModifier(PRIVATE))
			return PsiUtil.ACCESS_LEVEL_PRIVATE;
		else if(member.hasModifier(PROTECTED))
			return PsiUtil.ACCESS_LEVEL_PROTECTED;
		else if(member.hasModifier(PACKAGE_LOCAL))
			return PsiUtil.ACCESS_LEVEL_PACKAGE_LOCAL;
		else if(member.hasModifier(PUBLIC))
			return PsiUtil.ACCESS_LEVEL_PUBLIC;
		
		return PsiUtil.ACCESS_LEVEL_PACKAGE_LOCAL;
	}
}