/**
 *
 */
package networking.mesh;

/**
 * @author emily
 *
 */
public interface Link extends Comparable<Link>
{

	/**
	 *
	 * @return Link ID
	 */
	int getID();

	/**
	 *
	 * @return is the link in use
	 */
	boolean inUse();
}
